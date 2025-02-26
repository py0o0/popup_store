package com.example.post.repository;

import com.example.post.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
    @Query("SELECT bf.bf FROM BoardFile bf WHERE bf.boardId = :boardId")
    List<String> findByBoardId(long boardId);
}
