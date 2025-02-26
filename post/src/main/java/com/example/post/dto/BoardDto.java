package com.example.post.dto;

import lombok.Data;

@Data
public class BoardDto {
    private long boardId;

    String email;
    String name;
    String content;
    String created;

    long popId;
    long cnt;
    long heart;

    int isFile;
}
