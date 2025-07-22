package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransactionHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRepository;

    public void handle(Transaction transaction) {
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return; // invalid sender/recipient
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        double amount = transaction.getAmount().doubleValue();
        if (sender.getBalance() < amount) {
            return; // not enough balance
        }

        // Example: incentive is 1% of the amount
        double incentive = amount * 0.01;

        // update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        // save back to DB
        userRepository.save(sender);
        userRepository.save(recipient);

        // record the transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(transaction.getAmount());
        record.setIncentive(incentive);

        transactionRepository.save(record);
    }
}
