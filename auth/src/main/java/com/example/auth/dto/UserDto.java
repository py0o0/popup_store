package com.example.auth.dto;

import lombok.Data;

@Data
public class UserDto {
    String email;
    String password;
    String nickname;
    String address;
    String phone;
    String birth;

    String role;
    long point;
}
