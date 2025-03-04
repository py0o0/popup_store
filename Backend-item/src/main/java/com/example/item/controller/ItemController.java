package com.example.item.controller;

import com.example.item.dto.ItemDto;
import com.example.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    // 선택한 popId를 기반으로 아이템 등록
    @PostMapping("/{popId}")
    public ResponseEntity<?> createItem(
            @PathVariable Long popId,
            @RequestPart("itemDto") ItemDto itemDto,
            @RequestPart(name = "files", required = false) List<MultipartFile> files) throws IOException {
        itemService.saveItem(popId, itemDto, files);
        return ResponseEntity.ok().body(itemDto);
    }

    // 선택한 popId를 기반으로 아이템 보기
    @GetMapping("/{popId}")
    public ResponseEntity<?> getAllItemsByPopId(@PathVariable Long popId) {
        return itemService.getAllItemsByPopId(popId);
    }

    // 아이템 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllItems() {
        return itemService.getAllItems();
    }

    // 선택한 아이템 수정
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long itemId,
            @RequestPart("itemDto") ItemDto itemDto,
            @RequestPart(name = "files", required = false) List<MultipartFile> files) throws IOException {
        itemService.updateItem(itemId, itemDto, files);
        return ResponseEntity.ok().body(itemDto);
    }

    // 선택한 아이템 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // 검색을 통한 아이템 조회
    @GetMapping("/search")
    public ResponseEntity<?> searchItems(@RequestParam String keyword) {
        return itemService.searchItems(keyword);
    }

    // 선택한 ItemId로 아이템 조회
    @GetMapping("/itemDetail/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {return itemService.getItem(itemId);}
}
