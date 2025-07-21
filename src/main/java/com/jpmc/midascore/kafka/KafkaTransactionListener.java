package com.jpmc.midascore.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.foundation.Transaction;

@Component
public class KafkaTransactionListener {

    @Value("${general.kafka-topic}")
    private String topic;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Transaction transaction) {
        System.out.println("Receive transactions :" + transaction);
    }

}
