package com.example.soul.controller;

import com.example.soul.entity.Board;
import com.example.soul.service.BoardService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // =========================
    // 전체 조회 (정렬 + 필터 + 검색)
    // =========================
    @GetMapping
    public List<Board> getBoards(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "전체")   String category,
            @RequestParam(required = false)        String keyword,
            @RequestParam(required = false)        String currentUsername
    ) {
        return boardService.getBoards(
                sort,
                category,
                keyword,
                currentUsername
        );
    }

    // =========================
    // 게시글 작성
    // =========================
    @PostMapping
    public Board createBoard(@RequestBody Board board) {

        if (board.getTitle() == null ||
                board.getTitle().trim().isEmpty()) {
            throw new RuntimeException("title 필요");
        }

        if (board.getContent() == null ||
                board.getContent().trim().isEmpty()) {
            throw new RuntimeException("content 필요");
        }

        if (board.getUsername() == null ||
                board.getUsername().trim().isEmpty()) {
            throw new RuntimeException("username 필요");
        }

        if (board.getDate() == null ||
                board.getDate().isEmpty()) {
            board.setDate(LocalDate.now().toString());
        }

        return boardService.saveBoard(board);
    }

    // =========================
    // 상세 조회
    // =========================
    @GetMapping("/{id}")
    public Board getBoard(
            @PathVariable Long id,
            @RequestParam(required = false) String currentUsername
    ) {
        return boardService.getBoard(id, currentUsername);
    }

    // =========================
    // 카테고리 조회
    // =========================
    @GetMapping("/category/{category}")
    public List<Board> getCategoryBoards(
            @PathVariable String category
    ) {
        return boardService.getByCategory(category);
    }

    // =========================
    // 검색
    // =========================
    @GetMapping("/search")
    public List<Board> searchBoards(
            @RequestParam String keyword
    ) {
        return boardService.search(keyword);
    }

    // =========================
    // 좋아요 토글
    // =========================
    @PostMapping("/{id}/like")
    public Board toggleLike(
            @PathVariable Long id,
            @RequestBody  Map<String, String> body
    ) {
        String username = body.get("username");

        if (username == null || username.isBlank()) {
            throw new RuntimeException("username 필요");
        }

        return boardService.toggleLike(id, username);
    }

    // =========================
    // 삭제 (본인 + ADMIN)
    // =========================
    @DeleteMapping("/{id}")
    public void deleteBoard(
            @PathVariable Long id,
            @RequestParam  String username
    ) {
        boardService.deleteBoard(id, username);
    }
}