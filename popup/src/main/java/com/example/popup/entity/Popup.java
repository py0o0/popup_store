package com.example.popup.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="pop_up")
@NoArgsConstructor
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long popId;

    private String title;
    private String email;
    private String content;
    private String start;
    private String exp;
    private String offline;
    private String address;
    private String category;
    private String image;
}
