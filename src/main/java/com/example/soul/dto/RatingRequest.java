package com.example.soul.dto;

/**
 * 用户评分请求 DTO
 * 사용자 평가 요청 DTO
 */
public class RatingRequest {

    /**
     * 聊天室ID
     */
    private Long roomId;

    /**
     * 当前评分人用户名
     */
    private String username;

    /**
     * 评分
     * 1~5
     */
    private Integer score;

    public RatingRequest() {
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}