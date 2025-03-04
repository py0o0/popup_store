package com.example.item.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name="item")
@Data
@NoArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private Long popId;
    private String name;
    private Long amount;
    private Long price;
    private String des;
    private String email;
    int isFile;
    private String image;

    @Builder
    public ItemEntity(Long itemId, Long popId, String name, Long amount, Long price, String des, String email, int isFile, String image) {
        this.itemId = itemId;
        this.popId = popId;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.des = des;
        this.email = email;
        this.isFile = isFile;
        this.image = image;
    }
}


