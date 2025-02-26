package com.example.post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.post.dto.BoardDto;
import com.example.post.dto.CommentDto;
import com.example.post.entity.*;
import com.example.post.jwt.JwtUtil;
import com.example.post.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    private final BoardHeartRepository boardHeartRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final AwsS3Service awsS3Service;

    public ResponseEntity<?> write(String token,String name, String content,
                                    List<MultipartFile> files) throws IOException {
        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        Board board = new Board();
        board.setName(name);
        board.setContent(content);
        board.setEmail(email);

        if(files == null){
            board.setIsFile(0);
            boardRepository.save(board);
        }
        else{
            board.setIsFile(1);
            boardRepository.save(board);

            for(MultipartFile file : files){
                try{
                    awsS3Service.upload(file);
                    String url = awsS3Service.upload(file);

                    BoardFile boardFile = new BoardFile();
                    boardFile.setBoardId(board.getBoardId());
                    boardFile.setBf(url);
                    boardFileRepository.save(boardFile);
                }catch(Exception e){
                    return ResponseEntity.badRequest().body("파일 형식이 잘못됨");
                }
            }
        }

        return ResponseEntity.ok("게시글 작성 완료");
    }


    public ResponseEntity<?> getAll(int size, int page) {
        Sort sortByIdDesc = Sort.by(Sort.Order.desc("boardId"));
        Pageable pageable = PageRequest.of(page, size, sortByIdDesc);
        Page<Board> boardList = boardRepository.findAll(pageable);

        List<BoardDto> boardDtoList = new ArrayList<>();
        List<String> nicknameList = new ArrayList<>();

        for(Board board : boardList) {
            BoardDto boardDto = new BoardDto();
            boardDto.setName(board.getName());
            boardDto.setContent(board.getContent());
            boardDto.setEmail(board.getEmail());
            boardDto.setBoardId(board.getBoardId());
            boardDto.setCnt(board.getCnt());
            boardDto.setHeart(board.getHeart());
            boardDto.setCreated(board.getCreated());
            boardDto.setPopId(board.getPopId());

            User user = userRepository.findById(board.getEmail()).orElse(null);

            nicknameList.add(user.getNickname());
            boardDtoList.add(boardDto);
        }
        return ResponseEntity.ok(Map.of(
                "board", boardDtoList,
                "nickname", nicknameList
        ));
    }

    public ResponseEntity<?> getPost(long boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);

        if(board == null){
            return ResponseEntity.badRequest().body("페이지가 존재하지 않습니다.");
        }

        board.setCnt(board.getCnt() + 1);  // 조회수 처리
        boardRepository.save(board);

        BoardDto boardDto = new BoardDto();
        boardDto.setBoardId(board.getBoardId());
        boardDto.setCnt(board.getCnt());
        boardDto.setCreated(board.getCreated());
        boardDto.setPopId(board.getPopId());
        boardDto.setHeart(board.getHeart());
        boardDto.setContent(board.getContent());
        boardDto.setName(board.getName());
        boardDto.setIsFile(board.getIsFile());
        boardDto.setEmail(board.getEmail());

        List<String> boardFile = boardFileRepository.findByBoardId(boardId);

        User user = userRepository.findById(board.getEmail()).orElse(null);
        String nickname = user.getNickname();

        List<Comment> comments = commentRepository.findByBoardId(boardId);
        List<CommentDto> commentDtoList = new ArrayList<>();
        List<String> commentNickname = new ArrayList<>();

        for(Comment comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setBoardId(comment.getBoardId());
            commentDto.setHeart(comment.getHeart());
            commentDto.setContent(comment.getContent());
            commentDto.setEmail(comment.getEmail());
            commentDto.setCmtId(comment.getCmtId());

            User cUser = userRepository.findById(comment.getEmail()).orElse(null);
            String cNickname = cUser.getNickname();

            commentNickname.add(cNickname);
            commentDtoList.add(commentDto);
        }

        return ResponseEntity.ok(Map.of(
                "board", boardDto,
                "boardNickname", nickname,
                "boardFile", boardFile,
                "comment", commentDtoList,
                "commentNickname", commentNickname
        ));
    }

    public ResponseEntity<?> search(String category,String keyword, int size, int page) {
        Sort sortByIdDesc = Sort.by(Sort.Order.desc("boardId"));
        Pageable pageable = PageRequest.of(page, size, sortByIdDesc);
        Page<Board> boardList;

        if(category.equals("name"))
            boardList = boardRepository.findByName(keyword,pageable);
        else if(category.equals("email"))
            boardList = boardRepository.findByEmail(keyword,pageable);
        else //if(category.equals("content"))
            boardList = boardRepository.findByContent(keyword,pageable);

        List<BoardDto> boardDtoList = new ArrayList<>();
        List<String> nicknameList = new ArrayList<>();

        for(Board board : boardList) {
            BoardDto boardDto = new BoardDto();
            boardDto.setName(board.getName());
            boardDto.setContent(board.getContent());
            boardDto.setEmail(board.getEmail());
            boardDto.setBoardId(board.getBoardId());
            boardDto.setCnt(board.getCnt());
            boardDto.setHeart(board.getHeart());
            boardDto.setCreated(board.getCreated());
            boardDto.setPopId(board.getPopId());

            User user = userRepository.findById(board.getEmail()).orElse(null);

            nicknameList.add(user.getNickname());
            boardDtoList.add(boardDto);
        }
        return ResponseEntity.ok(Map.of(
                "board", boardDtoList,
                "nickname", nicknameList
        ));
    }

    public ResponseEntity<?> update(String token, long boardId, String content) {
        // jwt에서 현재 로그인한 유저 이메일과 board의 이메일 확인 작업 추가
        Board board = boardRepository.findById(boardId).orElse(null);

        if(board == null)
            return ResponseEntity.badRequest().body("게시글이 존재하지 않습니다.");

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        if(!email.equals(board.getEmail()))
            return ResponseEntity.badRequest().body("작성자만 수정할 수 있습니다.");


        board.setContent(content);
        boardRepository.save(board);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    @Transactional
    public ResponseEntity<?> delete(String token, long boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);

        if(board == null)
            return ResponseEntity.badRequest().body("게시글이 존재하지 않습니다.");

        String email;
        String role;
        try{
            email = jwtUtil.getEmail(token);
            role = jwtUtil.getRole(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        if(!email.equals(board.getEmail()) && !role.equals("ADMIN") )
            return ResponseEntity.badRequest().body("작성자 or 관리자만 삭제할 수 있습니다.");

        boardRepository.deleteById(boardId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    public ResponseEntity<?> like(String token,long boardId) {

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지않은 토큰");
        }

        Optional<BoardHeart> boardHeart = boardHeartRepository.findByBoardIdAndEmail(boardId,email);

        if(boardHeart.isPresent())
            return ResponseEntity.badRequest().body("이미 좋아요 하신 상태입니다.");

        BoardHeart bh = new BoardHeart();
        bh.setEmail(email);
        bh.setBoardId(boardId);
        boardHeartRepository.save(bh);

        Board board = boardRepository.findById(boardId).orElse(null);
        board.setHeart(board.getHeart() + 1);
        boardRepository.save(board);

        return ResponseEntity.ok("게시글 좋아요");
    }

    @Transactional
    public ResponseEntity<?> deleteLike(String token,long boardId) {

        String email;
        try{
            email = jwtUtil.getEmail(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유효하지않은 토큰");
        }

        Optional<BoardHeart> boardHeart = boardHeartRepository.findByBoardIdAndEmail(boardId,email);

        if(!boardHeart.isPresent())
            return ResponseEntity.badRequest().body("좋아요 하지 않은 게시글입니다.");

        boardHeartRepository.delete(boardHeart.get());

        Board board = boardRepository.findById(boardId).orElse(null);
        board.setHeart(board.getHeart() - 1);
        boardRepository.save(board);

        return ResponseEntity.ok("좋아요가 취소되었습니다.");
    }

    public ResponseEntity<?> likePost(String email,int size,int page) {
        Sort sortByIdDesc = Sort.by(Sort.Order.desc("boardId"));
        Pageable pageable = PageRequest.of(page, size, sortByIdDesc);
        Page<BoardHeart> boardHeartList = boardHeartRepository.findByEmail(pageable,email);

        List<BoardDto> boardDtoList = new ArrayList<>();
        List<String> nicknameList = new ArrayList<>();

        for(BoardHeart boardHeart : boardHeartList){
            Board board = boardRepository.findById(boardHeart.getBoardId()).orElse(null);

            if(board == null) continue;

            BoardDto boardDto = new BoardDto();
            boardDto.setName(board.getName());
            boardDto.setContent(board.getContent());
            boardDto.setEmail(board.getEmail());
            boardDto.setBoardId(board.getBoardId());
            boardDto.setCnt(board.getCnt());
            boardDto.setHeart(board.getHeart());
            boardDto.setCreated(board.getCreated());
            boardDto.setPopId(board.getPopId());

            User user = userRepository.findById(board.getEmail()).orElse(null);

            nicknameList.add(user.getNickname());
            boardDtoList.add(boardDto);
        }
        return ResponseEntity.ok(Map.of(
                "board", boardDtoList,
                "nickname", nicknameList
        ));

    }
}
