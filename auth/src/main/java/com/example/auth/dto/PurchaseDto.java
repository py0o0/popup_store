package com.example.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDto {
    private List<ItemDto> items;
    private String buyer;
    private String seller;
}
