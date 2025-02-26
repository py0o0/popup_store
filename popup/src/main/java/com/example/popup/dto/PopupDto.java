package com.example.popup.dto;

import lombok.Data;

@Data
public class PopupDto {
    private long popId;

    private String title;
    private String email;
    private String content;
    private String start;
    private String exp;
    private String offline;
    private String address;
    private String category;
    private String image;
}
