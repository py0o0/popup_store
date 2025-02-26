package com.example.post.controller;

import com.example.post.entity.Board;
import com.example.post.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/write") //게시글 작성
    public ResponseEntity<?> write(@RequestHeader("Authorization") String token,
            String name, String content, List<MultipartFile> files) throws IOException {
        return boardService.write(token,name, content,files);
    }

    @GetMapping("/all") //게시글 전체 조회
    public ResponseEntity<?> all(int size, int page) {
        return boardService.getAll(size, page);
    }

    @GetMapping("/{boardId}") //게시글 상세 조회
    public ResponseEntity<?> get(@PathVariable long boardId) {
        return boardService.getPost(boardId);
    }

    @GetMapping("/search") // 게시글 검색
    public ResponseEntity<?> search(String category,String keyword, int size, int page) {
        return boardService.search(category,keyword, size, page);
    }

    @PostMapping("/update/{boardId}") // 게시글 수정
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,@PathVariable long boardId,String content) {
        return boardService.update(token,boardId,content);
    }

    @PostMapping("/delete/{boardId}") // 게시글 삭제,
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,@PathVariable long boardId) {
        return boardService.delete(token,boardId);
    }

    @PostMapping("/like/{boardId}")
    public ResponseEntity<?> like(@RequestHeader("Authorization") String token,@PathVariable long boardId) {
        return boardService.like(token,boardId);
    }

    @PostMapping("/delete/like/{boardId}")
    public ResponseEntity<?> deleteLike(@RequestHeader("Authorization") String token,@PathVariable long boardId) {
        return boardService.deleteLike(token,boardId);
    }

    @GetMapping("/likepost")
    public ResponseEntity<?> likePost(String email,int size, int page) {
        return boardService.likePost(email,size,page);
    }

}
