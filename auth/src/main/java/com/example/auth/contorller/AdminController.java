package com.example.auth.contorller;

import com.example.auth.dto.UserDto;
import com.example.auth.service.AuthService;
import com.example.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<?> join(UserDto userDto) {
        return authService.adminJoin(userDto);
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers(int size, int page) {
        return userService.getAllUsers(size, page);
    }

    @GetMapping("/report/all")
    public ResponseEntity<?> getAllReports(int size, int page) {
        return userService.getAllReports(size, page);
    }

    @GetMapping("/report/{reportId}")
    public ResponseEntity<?> getReport(@PathVariable long reportId) {
        return userService.getReport(reportId);
    }
}
