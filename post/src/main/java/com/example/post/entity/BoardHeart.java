package com.example.post.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="board_heart")
public class BoardHeart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bhId;
    private String email;
    private long boardId;
}
