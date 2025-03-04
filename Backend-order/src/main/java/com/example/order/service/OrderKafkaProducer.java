package com.example.order.service;

import com.example.order.dto.PurchaseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendPurchaseMessage(PurchaseDto purchaseDto) {
        try {
            String message = objectMapper.writeValueAsString(purchaseDto);
            kafkaTemplate.send("item_purchase", message);
            System.out.println("전송 성공: " + message );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("전송 실패: " + e.getMessage());
        }
    }
}

