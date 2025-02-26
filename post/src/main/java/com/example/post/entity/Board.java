package com.example.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name="board")
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    String email;
    String name;
    String content;
    String created;

    long popId;
    long cnt;
    long heart;

    int isFile;

    @PrePersist
    public void prePersist() {
        // 현재 날짜를 "yyyy-MM-dd" 형식으로 설정
        this.created = LocalDate.now().toString();
    }

    @Builder
    public Board(long boardId, String email, String name, String content, String created, long cnt, long popId, long heart, int isFile) {
        this.boardId = boardId;
        this.email = email;
        this.name = name;
        this.content = content;
        this.created = created;
        this.cnt = cnt;
        this.popId = popId;
        this.heart = heart;
        this.isFile = isFile;
    }
}
