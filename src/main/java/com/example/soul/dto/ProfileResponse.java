package com.example.soul.dto;

/**
 * 用户个人资料响应 DTO
 * 사용자 개인 프로필 응답 DTO
 */
public class ProfileResponse {

    // 用户账号
    private String username;

    // 用户昵称
    private String name;

    // 电话
    private String phone;

    // 邮箱
    private String email;

    // 地址
    private String address;

    // 头像
    private String avatar;

    // 性别
    private String gender;

    // 年龄
    private Integer age;

    // 我会的技能
    private String teachSkill;

    // 我想学的技能
    private String learnSkill;

    // 国籍
    private String nationality;

    // 学习时间段
    private String timeSlot;

    // 项目 / 奖项 / 证书
    private String projectAwards;

    // 想学习的等级
    private String skillWantLevel;

    // 会的等级
    private String skillOfferLevel;

    // 我会的技能 Quiz 分数
    private Integer teachQuizScore;

    // 我想学的技能 Quiz 分数
    private Integer learnQuizScore;

    // ===== 默认构造 =====

    public ProfileResponse() {
    }

    // ===== 全参构造 =====

    public ProfileResponse(
            String username,
            String name,
            String phone,
            String email,
            String address,
            String avatar,
            String gender,
            Integer age,
            String teachSkill,
            String learnSkill,
            String nationality,
            String timeSlot,
            String projectAwards,
            String skillWantLevel,
            String skillOfferLevel,
            Integer teachQuizScore,
            Integer learnQuizScore
    ) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.avatar = avatar;
        this.gender = gender;
        this.age = age;
        this.teachSkill = teachSkill;
        this.learnSkill = learnSkill;
        this.nationality = nationality;
        this.timeSlot = timeSlot;
        this.projectAwards = projectAwards;
        this.skillWantLevel = skillWantLevel;
        this.skillOfferLevel = skillOfferLevel;
        this.teachQuizScore = teachQuizScore;
        this.learnQuizScore = learnQuizScore;
    }

    // ===== Getter / Setter =====

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getTeachSkill() {
        return teachSkill;
    }

    public void setTeachSkill(String teachSkill) {
        this.teachSkill = teachSkill;
    }

    public String getLearnSkill() {
        return learnSkill;
    }

    public void setLearnSkill(String learnSkill) {
        this.learnSkill = learnSkill;
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
}