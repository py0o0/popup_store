package com.example.order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_table")  // order는 예약어라 order_table로 설정
@Data
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long itemId;
    private Long popId;
    private String email;  // 구매자 이메일
    private Long totalPrice;
    private String itemName;
    private String buyerName;
    private String buyerAddress;
    private String buyerPhone;
    private Long totalAmount;
    private String orderDate;

    private String imageUrl;
    private Long paymentId;

    @Builder
    public OrderEntity(Long itemId, Long popId, String email,
                       Long totalPrice, String itemName, String buyerName, String buyerAddress,
                       String buyerPhone, Long totalAmount, String orderDate,
                       String imageUrl, Long paymentId) {
        this.itemId = itemId;
        this.popId = popId;
        this.email = email;
        this.totalPrice = totalPrice;
        this.itemName = itemName;
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerPhone = buyerPhone;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.imageUrl = imageUrl;
        this.paymentId = paymentId;
    }
}