package com.example.auth.service;

import com.example.auth.dto.ChatDto;
import com.example.auth.dto.KafkaCommentDto;
import com.example.auth.dto.PurchaseDto;
import com.example.auth.entity.Chat;
import com.example.auth.entity.User;
import com.example.auth.repository.ChatRepository;
import com.example.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "item_purchase", groupId = "team")
    public void purchaseKafka(String message) {
        PurchaseDto purchaseDto;
        try{
            purchaseDto = objectMapper.readValue(message, PurchaseDto.class);
            System.out.println("구매 정보: " + purchaseDto.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        messagingTemplate.convertAndSend("/chat/sub/" + purchaseDto.getSeller(),
                "구매 알림 : " + purchaseDto);
    }

    @KafkaListener(topics = "comment", groupId = "team")
    public void commentKafka(String message) {
        KafkaCommentDto kafkaCommentDto;
        try{
            kafkaCommentDto = objectMapper.readValue(message, KafkaCommentDto.class);
            System.out.println("댓글 알림: " + kafkaCommentDto.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        messagingTemplate.convertAndSend("/chat/sub/" + kafkaCommentDto.getEmail(),
                "댓글 알림 : " + kafkaCommentDto.getBoardId() + " 게시글에 댓글 작성됨");
    }

    public void sendMessage(String sEmail, String rEmail, String content) {
        User ruser = userRepository.findById(rEmail).orElse(null);
        User suser = userRepository.findById(sEmail).orElse(null);

        if(ruser == null || suser == null)
            return;

        Chat chat = new Chat();
        chat.setSEmail(sEmail);
        chat.setREmail(rEmail);
        chat.setContent(content);

        chatRepository.save(chat);
        messagingTemplate.convertAndSend("/chat/sub/" + rEmail, "송신자 : " + sEmail + " 메세지 : " + content); // 수신자 이메일에 전송
    }

    public ResponseEntity<?> getChatHistory(String sEmail, String rEmail) {
        User ruser = userRepository.findById(rEmail).orElse(null);
        User suser = userRepository.findById(sEmail).orElse(null);

        if(ruser == null || suser == null)
            return ResponseEntity.badRequest().body("존재하지 않는 유저");

        List<Chat> chatList = chatRepository.findChatHistory(sEmail, rEmail);
        List<ChatDto> chatDtoList = new ArrayList<>();

        for(Chat chat : chatList) {
            ChatDto chatDto = new ChatDto();
            chatDto.setSEmail(chat.getSEmail());
            chatDto.setREmail(chat.getREmail());
            chatDto.setContent(chat.getContent());
            chatDto.setChatId(chat.getChatId());
            chatDto.setCreated(chat.getCreated());

            chatDtoList.add(chatDto);
        }
        return ResponseEntity.ok(chatDtoList);
    }
}
