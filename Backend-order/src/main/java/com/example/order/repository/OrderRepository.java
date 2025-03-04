package com.example.order.repository;

import com.example.order.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
    List<OrderEntity> findByEmailAndPaymentId(String email, Long paymentId);
    List<OrderEntity> findByPopId(Long popId);
}
