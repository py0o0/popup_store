package com.example.auth.service;

import com.example.auth.dto.ReportDto;
import com.example.auth.dto.UserDto;
import com.example.auth.entity.Follow;
import com.example.auth.entity.Report;
import com.example.auth.entity.User;
import com.example.auth.jwt.JwtUtil;
import com.example.auth.repository.FollowRepository;
import com.example.auth.repository.ReportRepository;
import com.example.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final FollowRepository followRepository;
    private final ReportRepository reportRepository;

    public ResponseEntity<?> follow(String token, String flwEmail) {

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        Follow follow = followRepository.findByEmailAndFlwEmail(email,flwEmail).orElse(null);

        if(follow != null)
            return ResponseEntity.badRequest().body("이미 팔로우가 되있는 상태");

        follow = new Follow();
        follow.setEmail(email);
        follow.setFlwEmail(flwEmail);
        followRepository.save(follow);
        return ResponseEntity.ok("팔로우 되었습니다");
    }

    @Transactional
    public ResponseEntity<?> unfollow(String token,  String flwEmail) {
        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        Follow follow = followRepository.findByEmailAndFlwEmail(email,flwEmail).orElse(null);

        if(follow == null)
            return ResponseEntity.badRequest().body("팔로우 하지 않은 상태");
        followRepository.delete(follow);
        return ResponseEntity.ok("언팔로우 되었습니다");
    }

    public ResponseEntity<?> getAllFollow(String email) {
        List<String> flwerList = followRepository.findByFlwEmail(email);
        List<String> flwingList = followRepository.findByEmail(email);

        List<String> flwerNick = new ArrayList<>();
        List<String> flwingNick = new ArrayList<>();
        long flwerCnt = 0;
        long flwingCnt = 0;

        for (String flwing : flwingList) {
            User user = userRepository.findById(flwing).orElse(null);
            flwingNick.add(user.getNickname());
            flwingCnt++;
        }
        for (String flwer : flwerList) {
            User user = userRepository.findById(flwer).orElse(null);
            flwerNick.add(user.getNickname());
            flwerCnt++;
        }

        return ResponseEntity.ok(Map.of(
                "flwerList", flwerList,
                "flwerNick", flwerNick,
                "flwerCnt", flwerCnt,
                "flwingList", flwingList,
                "flwingNick", flwingNick,
                "flwingCnt", flwingCnt
        ));
    }

    @Transactional
    public ResponseEntity<?> deleteUser(String token, String email) {
        String preEmail,role;
        try{
            preEmail = jwtUtil.getEmail(token);
            role = jwtUtil.getRole(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        User user = userRepository.findById(email).orElse(null);
        if(user == null)
            return ResponseEntity.badRequest().body("존재하지 않는 유저");

        if(!preEmail.equals(email) && !role.equals("ROLE_ADMIN"))
            return ResponseEntity.badRequest().body("관리자 혹은 자신만 탈퇴 가능");

        userRepository.deleteById(email);
        return ResponseEntity.ok("탈퇴되었습니다.");
    }

    public ResponseEntity<?> getAllUsers(int size, int page) {
        Sort sortById =Sort.by(Sort.Order.asc("email"));
        Pageable pageable = PageRequest.of(page, size , sortById);
        Page<User> users = userRepository.findAll(pageable);

        List<UserDto> userDtoList = new ArrayList<>();
        for(User user : users){
            UserDto userDto = new UserDto();
            userDto.setEmail(user.getEmail());
            userDto.setNickname(user.getNickname());
            userDto.setAddress(user.getAddress());
            userDto.setPhone(user.getPhone());
            userDto.setBirth(user.getBirth());

            userDtoList.add(userDto);
        }
        return ResponseEntity.ok(userDtoList);
    }

    public ResponseEntity<?> update(String token, String address, String birth, String phone, String nickname) {
        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }
        User user = userRepository.findById(email).orElse(null);
        if(user == null)
            return ResponseEntity.badRequest().body("존재하지 않는 유저");

        if(address!=null)
            user.setAddress(address);
        if(birth!=null)
            user.setBirth(birth);
        if(phone!=null)
            user.setPhone(phone);
        if(nickname!=null)
            user.setNickname(nickname);

        userRepository.save(user);
        return ResponseEntity.ok("유저 정보 수정 완료");
    }

    public ResponseEntity<?> report(String token, String email, String content) {
        String preEmail;
        try{
            preEmail = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }
        Report report = new Report();
        report.setIsCheck(0);
        report.setContent(content);
        report.setEmail(preEmail);
        report.setRpEmail(email);

        reportRepository.save(report);
        return ResponseEntity.ok("신고가 완료되었습니다.");
    }

    public ResponseEntity<?> getAllReports(int size, int page) {
        Sort sortById =Sort.by(Sort.Order.desc("reportId"));
        Pageable pageable = PageRequest.of(page, size , sortById);
        Page<Report> reports = reportRepository.findAll(pageable);

        List<ReportDto> reportDtos = new ArrayList<>();
        for(Report report : reports){
            ReportDto reportDto = new ReportDto();
            reportDto.setReportId(report.getReportId());
            reportDto.setIsCheck(report.getIsCheck());
            reportDto.setContent(report.getContent());
            reportDto.setEmail(report.getEmail());
            reportDto.setRpEmail(report.getRpEmail());
            reportDtos.add(reportDto);
        }
        return ResponseEntity.ok(reportDtos);
    }

    public ResponseEntity<?> getReport(long reportId) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if(report == null)
            return ResponseEntity.badRequest().body("신고 내용이 없습니다.");

        ReportDto reportDto = new ReportDto();
        reportDto.setReportId(report.getReportId());
        reportDto.setIsCheck(report.getIsCheck());
        reportDto.setContent(report.getContent());
        reportDto.setEmail(report.getEmail());
        reportDto.setRpEmail(report.getRpEmail());

        report.setIsCheck(1);
        reportRepository.save(report);

        return ResponseEntity.ok(reportDto);
    }

    public ResponseEntity<?> fillPoint(String token, long point) {
        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }
        User user = userRepository.findById(email).orElse(null);
        if(user == null)
            return ResponseEntity.badRequest().body("존재하지 않는 유저");

        user.setPoint(user.getPoint() + point);
        userRepository.save(user);
        return ResponseEntity.ok("포인트가 충전되었습니다. 현재 포인트 : " + user.getPoint());
    }
}
