package com.example.post.controller;

import com.example.post.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestHeader("Authorization") String token,String content, long boardId) throws JsonProcessingException {
        return commentService.write(token,content,boardId);
    }
    // 업데이트 딜리트 좋아요 좋취

    @PostMapping("/update/{cmtId}") // jwt로 이메일 검증
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,@PathVariable long cmtId, String content) {
        return commentService.update(token, cmtId, content);
    }

    @PostMapping("/delete/{cmtId}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,@PathVariable long cmtId) {
        return commentService.delete(token,cmtId);
    }

    @PostMapping("/like/{cmtId}")
    public ResponseEntity<?> like(@RequestHeader("Authorization") String token,@PathVariable long cmtId) {
        return commentService.like(token,cmtId);
    }

    @PostMapping("/delete/like/{cmtId}")
    public ResponseEntity<?> deleteLike(@RequestHeader("Authorization") String token,@PathVariable long cmtId) {
        return commentService.deleteLike(token,cmtId);
    }

}
