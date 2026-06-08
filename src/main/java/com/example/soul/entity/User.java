package com.example.soul.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    // 用户ID（主键）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户账号
    @Column(nullable = false, unique = true)
    private String username;

    // 用户密码
    private String password;

    // 用户权限
    // USER / ADMIN
    private String role;

    // 手机号
    private String phone;

    // 昵称 / 姓名
    private String name;

    // 邮箱
    private String email;

    // 地址
    private String address;

    // 用户头像
    private String avatar;

    // 性别
    private String gender;

    // 年龄
    private Integer age;

    // 擅长技能
    private String skillOffer;

    // 想学习技能
    private String skillWant;

    // 国籍
    private String nationality;

    // 学习时间段
    // 例如：weekday_morning
    private String timeSlot;

    // 项目 / 奖项 / 证书
    @Column(columnDefinition = "TEXT")
    private String projectAwards;

    // 擅长技能等级
    private String skillOfferLevel;

    // 想学习技能等级
    private String skillWantLevel;

    // 擅长技能 Quiz 分数
    @Column(name = "teach_quiz_score")
    private Integer teachQuizScore = 0;

    // 想学习技能 Quiz 分数
    @Column(name = "learn_quiz_score")
    private Integer learnQuizScore = 0;

    // 可交流时间
    private String availableTime;

    // 是否有项目经验
    private Boolean hasProject;

    // 项目详情
    @Column(columnDefinition = "TEXT")
    private String projectDetail;

    // 是否有获奖经历
    private Boolean hasAward;

    // 获奖详情
    @Column(columnDefinition = "TEXT")
    private String awardDetail;

    // 用户平均评分
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    // 用户评分次数
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    // 主题模式
    private String themeMode;

    // 背景风格
    private String backgroundStyle;

    // 字体大小
    private String fontSize;

    public User() {
    }

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // =========================
    // 用户权限
    // =========================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =========================
    // 基本信息
    // =========================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    // =========================
    // 技能信息
    // =========================

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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getProjectAwards() {
        return projectAwards;
    }

    public void setProjectAwards(String projectAwards) {
        this.projectAwards = projectAwards;
    }

    public String getSkillOfferLevel() {
        return skillOfferLevel;
    }

    public void setSkillOfferLevel(String skillOfferLevel) {
        this.skillOfferLevel = skillOfferLevel;
    }

    public String getSkillWantLevel() {
        return skillWantLevel;
    }

    public void setSkillWantLevel(String skillWantLevel) {
        this.skillWantLevel = skillWantLevel;
    }

    public Integer getTeachQuizScore() {
        return teachQuizScore;
    }

    public void setTeachQuizScore(Integer teachQuizScore) {
        this.teachQuizScore = teachQuizScore;
    }

    public Integer getLearnQuizScore() {
        return learnQuizScore;
    }

    public void setLearnQuizScore(Integer learnQuizScore) {
        this.learnQuizScore = learnQuizScore;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    // =========================
    // 项目 / 奖项
    // =========================

    public Boolean getHasProject() {
        return hasProject;
    }

    public void setHasProject(Boolean hasProject) {
        this.hasProject = hasProject;
    }

    public String getProjectDetail() {
        return projectDetail;
    }

    public void setProjectDetail(String projectDetail) {
        this.projectDetail = projectDetail;
    }

    public Boolean getHasAward() {
        return hasAward;
    }

    public void setHasAward(Boolean hasAward) {
        this.hasAward = hasAward;
    }

    public String getAwardDetail() {
        return awardDetail;
    }

    public void setAwardDetail(String awardDetail) {
        this.awardDetail = awardDetail;
    }

    // =========================
    // 评分信息
    // =========================

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

    // =========================
    // UI 设置
    // =========================

    public String getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}