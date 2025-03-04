package com.example.cart.repository;

import com.example.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByEmailAndItemId(String email, Long itemId);
    List<CartEntity> findByEmail(String email);
    void deleteByEmail(String email); // 주문 완료 후 장바구니 삭제 메소드
}
