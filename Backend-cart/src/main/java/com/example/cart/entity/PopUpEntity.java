package com.example.cart.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// 테스트를 위해 임시로 구현

@Entity
@Table(name = "popUp")
@Data
@NoArgsConstructor
public class PopUpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long popId;
    private String email;
    private String name;
    private String content;

    @Builder
    public PopUpEntity(String email, String name, String content) {
        this.email = email;
        this.name = name;
        this.content = content;
    }
}

