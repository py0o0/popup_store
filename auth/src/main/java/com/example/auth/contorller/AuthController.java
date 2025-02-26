package com.example.auth.contorller;

import com.example.auth.dto.UserDto;
import com.example.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(String email, String password, HttpServletResponse res) {
        return authService.login(email,password,res);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(UserDto userDto) {
        return authService.join(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        return authService.logout(req, res);
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> reissue(String email,HttpServletResponse res) {
        return authService.reissue(email,res);
    }
}
