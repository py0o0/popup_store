package com.example.cart.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private Long itemId;
    private String email;
    private Long price;
    private Long amount;
    private String itemName;
    private Long popId; // popUpEntity의 ID만 전달
    private String imageUrl;

    @Builder
    public CartDto(Long cartId, Long itemId, String email, Long price, Long amount, String itemName, Long popId, String imageUrl) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.email = email;
        this.price = price;
        this.amount = amount;
        this.itemName = itemName;
        this.popId = popId;
        this.imageUrl = imageUrl;
    }
}