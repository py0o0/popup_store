package com.example.popup.service;

import com.example.popup.dto.PopupDto;
import com.example.popup.entity.Popup;
import com.example.popup.jwt.JwtUtil;
import com.example.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final AwsS3Service awsS3Service;
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> register(String token, PopupDto popupDto, MultipartFile file) {

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지않은 토큰");
        }

        Popup popup = new Popup();
        popup.setEmail(email);
        popup.setTitle(popupDto.getTitle());
        popup.setContent(popupDto.getContent());
        popup.setStart(popupDto.getStart());
        popup.setExp(popupDto.getExp());
        popup.setCategory(popupDto.getCategory());
        popup.setOffline(popupDto.getOffline());
        popup.setAddress(popupDto.getAddress());

        if(file == null){
            popup.setImage("");
        }
        else{
            try{
                String url = awsS3Service.upload(file);
                popup.setImage(url);
            }catch (Exception e){
                return ResponseEntity.badRequest().body("파일 형식이 잘못됨");
            }
        }
        popupRepository.save(popup);
        return ResponseEntity.ok("팝업 스토어 등록");
    }

    public ResponseEntity<?> getAll(String category, int page, int size) {
        Page<Popup> popups;
        if(category.equals("전체")) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "popId"));
            popups = popupRepository.findAll(pageable);
        }
        else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "popId"));
            popups = popupRepository.findByCategory(pageable, category);
        }

        List<PopupDto> popupDtoList = new ArrayList<>();

        for(Popup popup: popups){
            PopupDto popupDto = new PopupDto();
            popupDto.setTitle(popup.getTitle());
            popupDto.setContent(popup.getContent());
            popupDto.setStart(popup.getStart());
            popupDto.setExp(popup.getExp());
            popupDto.setCategory(popup.getCategory());
            popupDto.setOffline(popup.getOffline());
            popupDto.setAddress(popup.getAddress());
            popupDto.setPopId(popup.getPopId());
            popupDto.setImage(popup.getImage());
            popupDto.setEmail(popup.getEmail());

            popupDtoList.add(popupDto);
        }
        return ResponseEntity.ok(popupDtoList);
    }

    public ResponseEntity<?> update(String token, PopupDto popupDto, MultipartFile file) {

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지않은 토큰");
        }

        Popup popup = popupRepository.findById(popupDto.getPopId()).orElse(null);

        if(popup == null)
            return ResponseEntity.badRequest().body("존재하지 않는 팝업 스토어");

        if(!popup.getEmail().equals(email))
            return ResponseEntity.badRequest().body("등록자만 수정가능");

        popup.setTitle(popupDto.getTitle());
        popup.setContent(popupDto.getContent());
        popup.setStart(popupDto.getStart());
        popup.setExp(popupDto.getExp());
        popup.setCategory(popupDto.getCategory());
        popup.setOffline(popupDto.getOffline());
        popup.setAddress(popupDto.getAddress());

        if(file!=null){
            String url = awsS3Service.upload(file);
            popup.setImage(url);
        }
        popupRepository.save(popup);
        return ResponseEntity.ok("팝업 스토어 수정");
    }

    public ResponseEntity<?> getId(long popupId) {
        Popup popup = popupRepository.findById(popupId).orElse(null);
        if(popup == null)
            return ResponseEntity.badRequest().body("존재하지않는 팝업 스토어");

        PopupDto popupDto = new PopupDto();
        popupDto.setTitle(popup.getTitle());
        popupDto.setContent(popup.getContent());
        popupDto.setStart(popup.getStart());
        popupDto.setExp(popup.getExp());
        popupDto.setCategory(popup.getCategory());
        popupDto.setOffline(popup.getOffline());
        popupDto.setAddress(popup.getAddress());
        popupDto.setPopId(popup.getPopId());
        popupDto.setImage(popup.getImage());
        popupDto.setEmail(popup.getEmail());
        return ResponseEntity.ok(popupDto);
    }

    public ResponseEntity<?> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "popId"));
        Page<Popup> popups = popupRepository.findByTitle(pageable,keyword);

        List<PopupDto> popupDtoList = new ArrayList<>();

        for(Popup popup: popups){
            PopupDto popupDto = new PopupDto();
            popupDto.setTitle(popup.getTitle());
            popupDto.setContent(popup.getContent());
            popupDto.setStart(popup.getStart());
            popupDto.setExp(popup.getExp());
            popupDto.setCategory(popup.getCategory());
            popupDto.setOffline(popup.getOffline());
            popupDto.setAddress(popup.getAddress());
            popupDto.setPopId(popup.getPopId());
            popupDto.setImage(popup.getImage());
            popupDto.setEmail(popup.getEmail());

            popupDtoList.add(popupDto);
        }
        return ResponseEntity.ok(popupDtoList);
    }
}
