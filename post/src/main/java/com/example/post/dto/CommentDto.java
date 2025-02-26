package com.example.post.dto;

import lombok.Data;

@Data
public class CommentDto {
    private long cmtId;
    private long boardId;
    private String email;
    private String content;
    private long heart;
}
