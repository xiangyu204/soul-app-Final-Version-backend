package com.example.soul.dto.quiz;

// 前端开始测试时传来的数据
public class QuizStartRequest {

    // 测试技能，例如 React、Java、Python
    private String skill;

    // 测试类型：teach = 我会的技能，learn = 我想学的技能
    private String quizType;

    public String getSkill() {
        return skill;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }
}