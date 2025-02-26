package com.example.post.repository;

import com.example.post.entity.BoardHeart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardHeartRepository extends JpaRepository<BoardHeart, Long> {
    Optional<BoardHeart> findByBoardIdAndEmail(long boardId, String email);


    Page<BoardHeart> findByEmail(Pageable pageable, String email);
}
