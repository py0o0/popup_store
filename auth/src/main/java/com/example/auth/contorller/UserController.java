package com.example.auth.contorller;

import com.example.auth.entity.User;
import com.example.auth.jwt.JwtUtil;
import com.example.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestHeader("Authorization") String token, String flwEmail) {
        return userService.follow(token,flwEmail);
    }

    @PostMapping("/delete/follow")
    public ResponseEntity<?> unfollow(@RequestHeader("Authorization") String token, String flwEmail) {
        return userService.unfollow(token,flwEmail);
    }

    @GetMapping("/follow/all")
    public ResponseEntity<?> getAllFollowedUsers(String email) {
        return userService.getAllFollow(email);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, String email) {
        return userService.deleteUser(token,email);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        String address, String birth, String phone, String nickname) {
        return userService.update(token,address,birth,phone,nickname);
    }

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestHeader("Authorization") String token, String email, String content){
        return userService.report(token, email, content);
    }

    @PostMapping("/fill/point")
    public ResponseEntity<?> fillPoint(@RequestHeader("Authorization") String token, long point){
        return userService.fillPoint(token,point);
    }
}
