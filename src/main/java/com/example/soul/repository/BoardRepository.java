package com.example.soul.repository;

import com.example.soul.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByCategory(String category);

    List<Board> findByTitleContaining(String keyword);

    List<Board> findAllByOrderByLikesDesc();

    List<Board> findAllByOrderByCommentCountDesc();

    @Query("SELECT b FROM Board b WHERE " +
            "(:category = '전체' OR b.category = :category) AND " +
            "(:keyword IS NULL OR b.title LIKE %:keyword%)")
    List<Board> findByCategoryAndKeyword(
            @Param("category") String category,
            @Param("keyword")  String keyword
    );
}