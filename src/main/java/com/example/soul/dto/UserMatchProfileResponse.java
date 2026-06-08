package com.example.soul.dto;

import java.util.List;

/**
 * 用户匹配详情弹窗返回DTO
 * 사용자 매칭 상세 정보 DTO
 */
public class UserMatchProfileResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 擅长技能
     */
    private String skillOffer;

    /**
     * 想学习技能
     */
    private String skillWant;

    /**
     * 平均评分
     */
    private Double averageRating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 匹配历史记录
     */
    private List<MatchHistoryResponse> histories;

    public UserMatchProfileResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSkillOffer() {
        return skillOffer;
    }

    public void setSkillOffer(String skillOffer) {
        this.skillOffer = skillOffer;
    }

    public String getSkillWant() {
        return skillWant;
    }

    public void setSkillWant(String skillWant) {
        this.skillWant = skillWant;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<MatchHistoryResponse> getHistories() {
        return histories;
    }

    public void setHistories(List<MatchHistoryResponse> histories) {
        this.histories = histories;
    }
}