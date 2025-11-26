package org.razz.producer.controller;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/send")
    public String sendMessage(@RequestBody Map<String, Object> message) {
        String key = (String) message.getOrDefault("key", "default-key");
        ProducerRecord<String, Object> record = new ProducerRecord<>("events", key, message);
        kafkaTemplate.send(record);
        return "Email sent to Kafka - To: " + message.get("email") + ", Subject: " + message.get("subject");
    }
}