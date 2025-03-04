package com.example.cart.repository;

import com.example.cart.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByPopId(Long popId);
    List<ItemEntity> findByNameContainingOrDesContaining(String name, String des);
    List<ItemEntity> findListByPopIdAndItemId(Long popId, Long itemId);
}
