package com.example.item.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="itemFile")
public class ItemFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long fileId;

    long itemId;
    String itemFile;
}
