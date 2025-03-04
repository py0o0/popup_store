package com.example.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemDto {
    private Long itemId;
    private Long popId;
    private String name;
    private Long amount;
    private Long price;
    private String des;
    private String email;
    private List<String> itemFiles;
    private String image;

    @Builder
    public ItemDto(Long itemId, Long popId, String name, Long amount, Long price, String des, String email, List<String> itemFiles,
                   String image) {
        this.itemId = itemId;
        this.popId = popId;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.des = des;
        this.email = email;
        this.itemFiles = itemFiles;
        this.image = image;
    }
}
