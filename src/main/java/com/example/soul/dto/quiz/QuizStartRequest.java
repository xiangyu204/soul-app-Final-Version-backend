package com.example.soul.dto.quiz;

// 前端开始测试时传来的数据
public class QuizStartRequest {

    // 测试技能
    private String skill;

    // teach / learn
    private String quizType;

    // 题目语言
    private String targetLang;
    // 当前登录用户
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkill() {
        return skill;
    }

    public String getQuizType() {
        return quizType;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }
}