package com.example.auth.repository;

import com.example.auth.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE (c.sEmail = :sEmail AND c.rEmail = :rEmail) OR (c.sEmail = :rEmail AND c.rEmail = :sEmail) ORDER BY c.created ASC")
    List<Chat> findChatHistory(@Param("sEmail") String sEmail, @Param("rEmail") String rEmail);
}
