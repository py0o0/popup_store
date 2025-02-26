package com.example.auth.dto;

import lombok.Data;

@Data
public class KafkaCommentDto {
    private String email;
    private long boardId;
}
