package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    // Use the properly configured KafkaTemplate
    public KafkaProducer(@Value("${general.kafka-topic}") String topic,
            KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        String cleanedLine = transactionLine.replaceAll("\\s", "");
        String[] parts = cleanedLine.split(",");

        long senderId = Long.parseLong(parts[0]);
        long receiverId = Long.parseLong(parts[1]);
        float amount = Float.parseFloat(parts[2]);

        Transaction transaction = new Transaction();
        transaction.setSenderId(senderId);
        transaction.setRecipientId(receiverId);
        transaction.setAmount(BigDecimal.valueOf(amount));

        kafkaTemplate.send(topic, transaction);
    }
}