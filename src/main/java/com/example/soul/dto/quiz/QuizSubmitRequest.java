package com.example.soul.dto.quiz;

import java.util.List;

// Quiz 提交请求 DTO
public class QuizSubmitRequest {

    // 测试编号
    private Long quizId;

    // 当前登录用户账号
    private String username;

    // 测试技能
    private String skill;

    // 测试类型
    // teach = 我会的技能测试
    // learn = 我想学的技能测试
    private String quizType;

    // 用户提交的答案列表
    private List<QuizAnswerRequest> answers;

    public QuizSubmitRequest() {
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public List<QuizAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerRequest> answers) {
        this.answers = answers;
    }
}