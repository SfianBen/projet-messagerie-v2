package com.example.srvtranslate;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaListenerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${libretranslate.url}")
    private String libretranslateUrl;

    public KafkaListenerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "topicout", groupId = "srv-translate-group")
    public void listen(ConsumerRecord<String, String> record) {
        String originalMessage = record.value();
        System.out.println(" Message reçu : " + originalMessage);

        String[] parts = originalMessage.split("#");
        if (parts.length != 3) {
            System.out.println(" Format incorrect, ignoré.");
            return;
        }

        String from = parts[0].replace("FROM:", "");
        String to = parts[1].replace("TO:", "");
        String msg = parts[2].replaceAll("^\"|\"$", "");

        String translated = translateText(msg);
        String formatted = "FROM:" + from + "#TO:" + to + "#\"" + translated + "\"";

        kafkaTemplate.send("topicin", formatted);
        System.out.println(" Message traduit publié : " + formatted);
    }

    private String translateText(String input) {
        String url = libretranslateUrl + "/translate";

        Map<String, String> payload = new HashMap<>();
        payload.put("q", input);
        payload.put("source", "en");
        payload.put("target", "fr");
        payload.put("format", "text");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return (String) response.getBody().get("translatedText");
        } catch (Exception e) {
            System.err.println(" Erreur de traduction : " + e.getMessage());
            return input;
        }
    }
}
