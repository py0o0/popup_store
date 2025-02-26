package com.example.auth.service;

import com.example.auth.dto.UserDto;
import com.example.auth.entity.User;
import com.example.auth.jwt.JwtUtil;
import com.example.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public ResponseEntity<?> login(String email, String password, HttpServletResponse res) {
        User user = userRepository.findById(email).orElse(null);

        if (user == null)
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");

        // 인증 안한 계정 (enabled = 0 일 시 로그인 실패 기능 추가)

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("비밀번호 불일치");
        }
        String role = user.getRole();
        String accessToken = jwtUtil.createToken(email, role, "access");

        tokenService.removeRefreshToken(email);     //리프레시 토큰 제거
        String refreshToken = jwtUtil.createToken(email, role, "refresh");
        tokenService.setRefreshToken(email, refreshToken);

        res.addCookie(creatCookie("refresh", refreshToken)); //리프레시는 쿠키로 전송
        res.addHeader("access-token", accessToken);
        UserDto userDto = new UserDto();

        userDto.setEmail(email);
        userDto.setBirth(user.getBirth());
        userDto.setNickname(user.getNickname());
        userDto.setPhone(user.getPhone());
        userDto.setPoint(user.getPoint());
        userDto.setRole(role);
        return ResponseEntity.ok(userDto);
    }

    private Cookie creatCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public ResponseEntity<?> join(UserDto userDto) {
        User nuser = userRepository.findById(userDto.getEmail()).orElse(null);

        if(nuser != null)
            return ResponseEntity.ok("이미 존재하는 ID입니다");


        User user = User.builder()
                .email(userDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .birth(userDto.getBirth())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .nickname(userDto.getNickname())
                .role("ROLE_USER")
                .point(0)
                .enabled(0)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        String refresh = null;
        Cookie[] cookies = req.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        String email = jwtUtil.getEmail(refresh);
        tokenService.removeRefreshToken(email);

        Cookie cookie = new Cookie("refresh", refresh);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
        return ResponseEntity.ok("로그아웃 성공");
    }

    public ResponseEntity<?> reissue(String email, HttpServletResponse res) {
        String refresh = tokenService.getRefreshToken(email);
        if(refresh == null)
            return ResponseEntity.badRequest().body("refresh 존재하지 않음");
        try{
            jwtUtil.isExpired(refresh);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("refresh 토큰 만료. 재로그인 바람");
        }
        String role = jwtUtil.getRole(refresh);
        String accessToken = jwtUtil.createToken(email, role, "access");

        res.addHeader("AccessToken", accessToken);
        return ResponseEntity.ok("access token 재발급 성공");
    }

    public ResponseEntity<?> adminJoin(UserDto userDto) {
        User nuser = userRepository.findById(userDto.getEmail()).orElse(null);

        if(nuser != null)
            return ResponseEntity.ok("이미 존재하는 ID입니다");


        User user = User.builder()
                .email(userDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .birth(userDto.getBirth())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .nickname(userDto.getNickname())
                .role("ROLE_ADMIN")
                .point(0)
                .enabled(0)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
