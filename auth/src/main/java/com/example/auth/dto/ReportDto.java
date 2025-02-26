package com.example.auth.dto;

import lombok.Data;

@Data
public class ReportDto {
    private long reportId;

    private String email;
    private String rpEmail;
    private String content;
    private int isCheck;
}
