package com.example.item.repository;

import com.example.item.entity.PopUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PopUpRepository extends JpaRepository<PopUpEntity, Long> {
    Optional<PopUpEntity> findByEmail(String email);
    Optional<PopUpEntity> findByPopId(Long popId);
}
