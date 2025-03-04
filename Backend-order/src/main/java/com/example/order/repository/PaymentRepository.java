package com.example.order.repository;

import com.example.order.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByBuyerEmail(String buyerEmail);
    PaymentEntity findByPaymentId(long paymentId);
//    List<PaymentEntity> findByPopId(long popId);
}
