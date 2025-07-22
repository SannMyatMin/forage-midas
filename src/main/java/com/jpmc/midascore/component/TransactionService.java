package com.jpmc.midascore.component;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRecordRepository recordRepo;

    @KafkaListener(topics = "midas-transactions", groupId = "midas-core")
    public void handleTransaction(TransactionRecord txn) {
        Optional<UserRecord> senderOpt = userRepo.findById(txn.getSender().getId());
        Optional<UserRecord> recipientOpt = userRepo.findById(txn.getRecipient().getId());

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= txn.getAmount()) {
                // Deduct and add balances
                sender.setBalance(sender.getBalance() - txn.getAmount());
                recipient.setBalance(recipient.getBalance() + txn.getAmount());

                // Save users
                userRepo.save(sender);
                userRepo.save(recipient);

                // Save transaction
                TransactionRecord record = new TransactionRecord();
                record.setAmount(txn.getAmount());
                record.setSender(sender);
                record.setRecipient(recipient);
                recordRepo.save(record);
            }
        }
    }
}
