package com.example.popup.controller;

import com.example.popup.dto.PopupDto;
import com.example.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/popup")
public class PopupController {
    private final PopupService popupService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader("Authorization") String token,
                                      PopupDto popupDto, MultipartFile file) { // DTO는 popId email image 빼고 전부
        return popupService.register(token,popupDto,file);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(String category, int page, int size) {
        return popupService.getAll(category,page, size);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,
                                    PopupDto popupDto, MultipartFile file) { // Dto는 popId email image 빼고 전부
        return popupService.update(token, popupDto, file);
    }

    @GetMapping("/{popupId}")
    public ResponseEntity<?> getId(@PathVariable long popupId) { // Dto는 popId email image 빼고 전부
        return popupService.getId(popupId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(String keyword,int page, int size) {
        return popupService.search(keyword,page,size);
    }
}
