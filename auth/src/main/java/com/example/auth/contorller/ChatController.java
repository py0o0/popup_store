package com.example.auth.contorller;

import com.example.auth.dto.ChatDto;
import com.example.auth.entity.Chat;
import com.example.auth.repository.ChatRepository;
import com.example.auth.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 메세지 송신
    @MessageMapping("/send") // /chat/pub/send
    public void sendMessage(@Payload ChatDto chatDto) {
        String rEmail = chatDto.getREmail();
        System.out.println(chatDto);
        chatService.sendMessage(chatDto.getSEmail(), chatDto.getREmail(), chatDto.getContent());
    }

    @GetMapping("/chat/history") // 과거만 불러오고 현재는 웹소켓으로 인해 자동으로 보임
    public ResponseEntity<?> getChatHistory(String sEmail, String rEmail) {
        return chatService.getChatHistory(sEmail,rEmail);
    }
}


