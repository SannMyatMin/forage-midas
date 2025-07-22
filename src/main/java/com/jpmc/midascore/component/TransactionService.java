package com.jpmc.midascore.component;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRecordRepository recordRepo;

    @Autowired
    private RestTemplate restTemplate;

    private BigDecimal getIncentive(TransactionRecord txn) {
        String url = "http://localhost:8080/incentive";
        ResponseEntity<Incentive> response = restTemplate.postForEntity(url, txn, Incentive.class);
        Incentive incentive = response.getBody();
        return incentive != null ? incentive.getAmount() : BigDecimal.ZERO;
    }

    private boolean isTransactionValid(Transaction transaction) {
        return transaction.getSenderId() > 0 &&
                transaction.getRecipientId() > 0 &&
                transaction.getAmount() != null &&
                transaction.getAmount().doubleValue() > 0;
    }

    private BigDecimal getIncentive(Transaction transaction) {
        String url = "http://localhost:8080/incentive";
        try {
            ResponseEntity<Incentive> response = restTemplate.postForEntity(url, transaction, Incentive.class);
            Incentive incentive = response.getBody();
            return incentive != null ? incentive.getAmount() : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @KafkaListener(topics = "midas-transactions", groupId = "midas-core")
    public void handleTransaction(TransactionRecord txnRecord) {
        Optional<UserRecord> senderOpt = userRepo.findById(txnRecord.getSender().getId());
        Optional<UserRecord> recipientOpt = userRepo.findById(txnRecord.getRecipient().getId());

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            // Validate sender's balance
            if (sender.getBalance() >= txnRecord.getAmount().doubleValue()) {
                // Prepare a Transaction DTO for Incentive API
                Transaction transaction = new Transaction();
                transaction.setSenderId(sender.getId());
                transaction.setRecipientId(recipient.getId());
                transaction.setAmount(txnRecord.getAmount());

                // 1. Validate transaction (if you have additional validation logic)
                if (isTransactionValid(transaction)) {
                    // 2. Call Incentive API to get incentive amount
                    BigDecimal incentiveAmount = getIncentive(transaction);

                    // 3. Update balances
                    sender.setBalance(sender.getBalance() - txnRecord.getAmount().doubleValue());
                    recipient.setBalance(recipient.getBalance() + txnRecord.getAmount().doubleValue()
                            + incentiveAmount.doubleValue());

                    // 4. Save updated users
                    userRepo.save(sender);
                    userRepo.save(recipient);

                    // 5. Save transaction record with incentive amount
                    TransactionRecord record = new TransactionRecord();
                    record.setAmount(txnRecord.getAmount());
                    record.setSender(sender);
                    record.setRecipient(recipient);
                    record.setIncentive(incentiveAmount.doubleValue());
                    recordRepo.save(record);
                }
            }
        }
    }
    
}
