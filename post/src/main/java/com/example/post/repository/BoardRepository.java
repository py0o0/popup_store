package com.example.post.repository;

import com.example.post.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b WHERE b.name LIKE %:keyword%")
    Page<Board> findByName(String keyword, Pageable pageable);

    Page<Board> findByEmail(String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.content LIKE %:keyword%")
    Page<Board> findByContent(String keyword, Pageable pageable);
}
