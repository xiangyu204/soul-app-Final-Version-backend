package com.example.soul.service;

import com.example.soul.entity.Board;
import com.example.soul.entity.User;
import com.example.soul.repository.BoardRepository;
import com.example.soul.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository  userRepository;

    public BoardService(BoardRepository boardRepository,
                        UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository  = userRepository;
    }

    // =========================
    // 공통: 유저 정보 + liked 주입
    // =========================
    private void injectUserInfo(Board board, String currentUsername) {

        userRepository
                .findByUsername(board.getUsername())
                .ifPresent(user -> {
                    board.setName(user.getName());
                    board.setAvatar(user.getAvatar());
                });

        if (currentUsername != null && !currentUsername.isBlank()) {
            board.setLiked(board.getLikedBy().contains(currentUsername));
        }
    }

    // =========================
    // 전체 조회 + 정렬 + 필터
    // =========================
    public List<Board> getBoards(String sort,
                                 String category,
                                 String keyword,
                                 String currentUsername) {

        List<Board> boards;

        boolean hasCategory =
                category != null && !category.equals("전체");

        boolean hasKeyword =
                keyword != null && !keyword.isBlank();

        if (hasCategory || hasKeyword) {

            boards = boardRepository.findByCategoryAndKeyword(
                    category == null ? "전체" : category,
                    keyword
            );

        } else if ("likes".equals(sort)) {

            boards = boardRepository.findAllByOrderByLikesDesc();

        } else if ("comments".equals(sort)) {

            boards = boardRepository.findAllByOrderByCommentCountDesc();

        } else {

            boards = boardRepository.findAll();
        }

        if ("likes".equals(sort)) {
            boards.sort((a, b) -> b.getLikes() - a.getLikes());
        } else if ("comments".equals(sort)) {
            boards.sort((a, b) -> b.getCommentCount() - a.getCommentCount());
        }

        boards.forEach(b -> injectUserInfo(b, currentUsername));

        return boards;
    }

    // =========================
    // 저장
    // =========================
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    // =========================
    // 상세 조회
    // =========================
    public Board getBoard(Long id, String currentUsername) {

        Board board = boardRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        injectUserInfo(board, currentUsername);

        return board;
    }

    // =========================
    // 카테고리 조회
    // =========================
    public List<Board> getByCategory(String category) {
        return boardRepository.findByCategory(category);
    }

    // =========================
    // 검색
    // =========================
    public List<Board> search(String keyword) {
        return boardRepository.findByTitleContaining(keyword);
    }

    // =========================
    // 좋아요 토글
    // =========================
    public Board toggleLike(Long id, String username) {

        Board board = boardRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (board.getLikedBy().contains(username)) {
            board.getLikedBy().remove(username);
            board.setLikes(Math.max(0, board.getLikes() - 1));
        } else {
            board.getLikedBy().add(username);
            board.setLikes(board.getLikes() + 1);
        }

        Board saved = boardRepository.save(board);
        saved.setLiked(saved.getLikedBy().contains(username));

        return saved;
    }

    // =========================
    // 삭제 (본인 + ADMIN)
    // =========================
    public void deleteBoard(Long id, String username) {

        Board board = boardRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        String  role     = user.getRole() == null ? "" : user.getRole().trim();
        boolean isAuthor = username.equals(board.getUsername());
        boolean isAdmin  = "ADMIN".equalsIgnoreCase(role);

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("삭제 권한 없음");
        }

        boardRepository.deleteById(id);
    }
}