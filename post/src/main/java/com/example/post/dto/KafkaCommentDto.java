package com.example.post.dto;

import lombok.Data;

@Data
public class KafkaCommentDto {
    private String email;
    private long boardId;
}
