package com.example.popup.repository;

import com.example.popup.entity.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {
    Page<Popup> findByCategory(Pageable pageable, String category);

    @Query("SELECT p FROM Popup p WHERE p.title LIKE %:keyword%")
    Page<Popup> findByTitle(Pageable pageable, String keyword);
}
