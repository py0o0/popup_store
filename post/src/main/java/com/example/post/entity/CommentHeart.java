package com.example.post.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="comment_heart")
@NoArgsConstructor
public class CommentHeart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chId;
    private String email;
    private long cmtId;
}
