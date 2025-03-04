package com.example.order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "payment")
@Data
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private String orderItem;
    private String buyerEmail;
    private String orderDate;
    private Long totalPrice;

    @Builder
    public PaymentEntity(String orderItem, String buyerEmail, String orderDate, Long totalPrice) {
        this.orderItem = orderItem;
        this.buyerEmail = buyerEmail;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }
}
