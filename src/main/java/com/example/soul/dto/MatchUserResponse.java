package com.example.soul.dto;

import java.util.List;

/**
 * 匹配用户返回 DTO
 * 매칭 사용자 응답 DTO
 */
public class MatchUserResponse {

    /**
     * 用户ID
     * 사용자 ID
     */
    private Long id;

    /**
     * 用户账号
     * 사용자 계정
     */
    private String username;

    /**
     * 页面显示名称
     * 화면 표시 이름
     */
    private String name;

    /**
     * 年龄
     * 나이
     */
    private Integer age;

    /**
     * 性别
     * 성별
     */
    private String gender;

    /**
     * 国籍
     * 국적
     */
    private String nationality;

    /**
     * 用户头像
     * 사용자 프로필 이미지
     */
    private String avatar;

    /**
     * 擅长技能
     * 제공 가능한 기술
     */
    private List<String> skills;

    /**
     * 想学习技能
     * 배우고 싶은 기술
     */
    private List<String> wants;

    /**
     * 学习时间段
     * 학습 가능 시간대
     */
    private String timeSlot;

    /**
     * 想学习的技能等级
     * 배우고 싶은 기술 수준
     */
    private String skillWantLevel;

    /**
     * 擅长技能等级
     * 제공 가능한 기술 수준
     */
    private String skillOfferLevel;

    /**
     * 平均评分
     * 평균 평점
     */
    private Double averageRating;

    /**
     * 评分人数
     * 평가 인원 수
     */
    private Integer ratingCount;

    /**
     * 默认构造函数
     */
    public MatchUserResponse() {
    }

    /**
     * 全参构造函数
     */
    public MatchUserResponse(
            Long id,
            String username,
            String name,
            Integer age,
            String gender,
            String nationality,
            String avatar,
            List<String> skills,
            List<String> wants,
            String timeSlot,
            String skillWantLevel,
            String skillOfferLevel,
            Double averageRating,
            Integer ratingCount
    ) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.nationality = nationality;
        this.avatar = avatar;
        this.skills = skills;
        this.wants = wants;
        this.timeSlot = timeSlot;
        this.skillWantLevel = skillWantLevel;
        this.skillOfferLevel = skillOfferLevel;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    // =========================
    // Getter / Setter
    // =========================

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getWants() {
        return wants;
    }

    public void setWants(List<String> wants) {
        this.wants = wants;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getSkillWantLevel() {
        return skillWantLevel;
    }

    public void setSkillWantLevel(String skillWantLevel) {
        this.skillWantLevel = skillWantLevel;
    }

    public String getSkillOfferLevel() {
        return skillOfferLevel;
    }

    public void setSkillOfferLevel(String skillOfferLevel) {
        this.skillOfferLevel = skillOfferLevel;
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
}