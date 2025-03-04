package com.example.order.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private String orderItem;
    private String buyerEmail;
    private String orderDate;
    private Long totalPrice;

    @Builder
    public PaymentDto(Long paymentId, String orderItem, String buyerEmail, String orderDate, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderItem = orderItem;
        this.buyerEmail = buyerEmail;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }
}
