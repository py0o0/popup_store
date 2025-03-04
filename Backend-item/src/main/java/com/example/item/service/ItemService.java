package com.example.item.service;

import com.example.item.dto.ItemDto;
import com.example.item.entity.ItemEntity;
import com.example.item.entity.ItemFile;
import com.example.item.entity.PopUpEntity;
import com.example.item.jwt.JwtUtil;
import com.example.item.repository.ItemFileRepository;
import com.example.item.repository.ItemRepository;
import com.example.item.repository.PopUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final PopUpRepository popUpRepository;
    private final ItemFileRepository itemFileRepository;
    private final AwsS3Service awsS3Service;

    // 아이템 생성
    public void saveItem(Long popId, ItemDto itemDto,
                         List<MultipartFile> files) throws IOException {
        PopUpEntity popUpEntity = popUpRepository.findByPopId(popId)
                .orElseThrow(() -> new IllegalArgumentException("해당 popId의 팝업 스토어가 존재하지 않습니다."));

        ItemEntity item = ItemEntity.builder()
                .itemId(itemDto.getItemId())
                .popId(popUpEntity.getPopId())
                .name(itemDto.getName())
                .amount(itemDto.getAmount())
                .price(itemDto.getPrice())
                .des(itemDto.getDes())
                .email(popUpEntity.getEmail())
                .build();

        itemRepository.save(item);

        if (files == null || files.isEmpty()) {
            item.setIsFile(0);
        } else {
            item.setIsFile(1);
            boolean isFirstFile = true; // 첫 번째 파일인지를 확인하기 위한 변수

            for (MultipartFile file : files) {
                try {
                    String url = awsS3Service.upload(file);
                    // 첫 이미지 URL만 item 필드에 삽입
                    if (isFirstFile) {
                        item.setImage(url);
                        isFirstFile = false; // 첫 번째 파일 설정 후 false로 변경
                    }
                    ItemFile itemFile = new ItemFile();
                    itemFile.setItemId(item.getItemId());
                    itemFile.setItemFile(url);
                    itemFileRepository.save(itemFile);
                } catch (Exception e) {
                    ResponseEntity.badRequest().body("파일 형식이 잘못되었습니다.");
                }
            }
        }

    }

    // 아이템 조회
    public ResponseEntity<?> getAllItemsByPopId(Long popId) {
        // PopUpEntity 조회
        PopUpEntity popUpEntity = popUpRepository.findByPopId(popId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팝업 ID를 가진 팝업 스토어가 존재하지 않습니다."));

        // 해당 팝업 ID를 가진 아이템 목록 조회
        List<ItemEntity> itemEntities = itemRepository.findByPopId(popUpEntity.getPopId());

        // itemId 리스트 추출
        List<Long> itemIds = itemEntities.stream()
                .map(ItemEntity::getItemId)
                .toList();

        // itemId별 itemFile 목록을 매핑
        Map<Long, List<String>> itemFileMap = itemIds.stream()
                .collect(Collectors.toMap(
                        itemId -> itemId,
                        itemFileRepository::findByItemId
                ));

        List<ItemDto> itemDtos = itemEntities.stream()
                .map(i -> ItemDto.builder()
                        .itemId(i.getItemId())
                        .popId(popUpEntity.getPopId())
                        .name(i.getName())
                        .amount(i.getAmount())
                        .price(i.getPrice())
                        .des(i.getDes())
                        .email(i.getEmail())
                        .itemFiles(itemFileMap.getOrDefault(i.getItemId(), new ArrayList<>()))
                        .image(i.getImage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDtos);
    }

    // 아이템 수정
    public void updateItem(Long itemId, ItemDto itemDto, List<MultipartFile> files) throws IOException {
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 itemId에 해당하는 아이템을 찾을 수 없습니다."));

        item.setName(itemDto.getName());
        item.setAmount(itemDto.getAmount());
        item.setPrice(itemDto.getPrice());
        item.setDes(itemDto.getDes());

        // 기존 이미지 삭제 및 새로운 이미지 저장
        if (files != null && !files.isEmpty()) {
            // 기존 파일 삭제
            List<ItemFile> existingFiles = itemFileRepository.findItemFileByItemId(itemId);
            for (ItemFile file : existingFiles) {
                awsS3Service.delete(file.getItemFile());
            }
            itemFileRepository.deleteAll(existingFiles);

            boolean isFirstFile = true; // 첫 번째 파일인지를 확인하기 위한 변수

            List<String> uploadedUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = awsS3Service.upload(file);
                uploadedUrls.add(url);

                if (isFirstFile) {
                    item.setImage(url);
                    isFirstFile = false; // 첫 번째 파일 설정 후 false로 변경
                }

                ItemFile itemFile = new ItemFile();
                itemFile.setItemId(item.getItemId());
                itemFile.setItemFile(url);
                itemFileRepository.save(itemFile);
            }

            item.setIsFile(1);
        } else {
            item.setIsFile(0);
        }

        itemRepository.save(item);
    }

    // 아이템 삭제
    public void deleteItem(Long itemId) {
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 itemId에 해당하는 아이템을 찾을 수 없습니다."));

        List<ItemFile> itemFiles = itemFileRepository.findItemFileByItemId(itemId);

        for (ItemFile file : itemFiles) {
            awsS3Service.delete(file.getItemFile());
        }
        itemFileRepository.deleteAll(itemFiles);
        itemRepository.delete(item);
    }

    // 전체 아이템 조회
    public ResponseEntity<?> getAllItems() {
        List<ItemEntity> itemEntities = itemRepository.findAll();

        List<Long> itemIds = itemEntities.stream()
                .map(ItemEntity::getItemId)
                .toList();

        Map<Long, List<String>> itemFileMap = itemIds.stream()
                .collect(Collectors.toMap(
                        itemId -> itemId,
                        itemFileRepository::findByItemId
                ));

        List<ItemDto> itemDtos = itemEntities.stream()
                .map(i -> ItemDto.builder()
                        .itemId(i.getItemId())
                        .popId(i.getPopId())
                        .name(i.getName())
                        .amount(i.getAmount())
                        .price(i.getPrice())
                        .des(i.getDes())
                        .email(i.getEmail())
                        .itemFiles(itemFileMap.getOrDefault(i.getItemId(), new ArrayList<>()))
                        .image(i.getImage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDtos);
    }

    // 검색(아이템 이름 또는 설명)을 통한 아이템들 조회
    public ResponseEntity<?> searchItems(String keyword) {
        // 검색된 아이템 목록 조회 (이름 또는 설명에 keyword 포함)
        List<ItemEntity> itemEntities = itemRepository.findByNameContainingOrDesContaining(keyword, keyword);

        List<Long> itemIds = itemEntities.stream()
                .map(ItemEntity::getItemId)
                .toList();

        Map<Long, List<String>> itemFileMap = itemIds.stream()
                .collect(Collectors.toMap(
                        itemId -> itemId,
                        itemFileRepository::findByItemId
                ));

        List<ItemDto> itemDtos = itemEntities.stream()
                .map(i -> ItemDto.builder()
                        .itemId(i.getItemId())
                        .popId(i.getPopId())
                        .name(i.getName())
                        .amount(i.getAmount())
                        .price(i.getPrice())
                        .des(i.getDes())
                        .email(i.getEmail())
                        .itemFiles(itemFileMap.getOrDefault(i.getItemId(), new ArrayList<>()))
                        .image(i.getImage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDtos);
    }

    // itemId로 아이템 검색
    public ResponseEntity<?> getItem(Long itemId) {
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 아이템을 찾을 수 없습니다."));

        List<Long> itemIds = Collections.singletonList(itemEntity.getItemId());

        Map<Long, List<String>> itemFileMap = itemIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        itemFileRepository::findByItemId
                ));

        ItemDto itemDto = ItemDto.builder()
                .itemId(itemEntity.getItemId())
                .popId(itemEntity.getPopId())
                .name(itemEntity.getName())
                .amount(itemEntity.getAmount())
                .price(itemEntity.getPrice())
                .des(itemEntity.getDes())
                .email(itemEntity.getEmail())
                .itemFiles(itemFileMap.getOrDefault(itemEntity.getItemId(), new ArrayList<>()))
                .image(itemEntity.getImage())
                .build();

        return ResponseEntity.ok(itemDto);
    }
}
