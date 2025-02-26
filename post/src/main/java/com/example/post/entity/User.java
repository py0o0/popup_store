package com.example.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    String email;

    String password;
    String nickname;
    String address;
    String role;
    String phone;
    String birth;
    int enabled;
    long point;
}
