package com.example.order.repository;

import com.example.order.entity.PopUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PopUpRepository extends JpaRepository<PopUpEntity, Long> {
    Optional<PopUpEntity> findByEmail(String email);
    Optional<PopUpEntity> findByPopId(Long popId);
}
