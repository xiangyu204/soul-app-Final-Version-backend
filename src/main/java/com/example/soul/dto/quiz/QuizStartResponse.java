package com.example.soul.dto.quiz;

import java.util.List;

// 开始测试后，返回给前端的数据
public class QuizStartResponse {

    // 这一次测试的编号
    private Long quizId;

    // 测试技能
    private String skill;

    // 测试类型 teach / learn
    private String quizType;

    // 题目列表
    private List<QuizQuestionResponse> questions;

    public QuizStartResponse(Long quizId, String skill, String quizType, List<QuizQuestionResponse> questions) {
        this.quizId = quizId;
        this.skill = skill;
        this.quizType = quizType;
        this.questions = questions;
    }

    public Long getQuizId() {
        return quizId;
    }

    public String getSkill() {
        return skill;
    }

    public String getQuizType() {
        return quizType;
    }

    public List<QuizQuestionResponse> getQuestions() {
        return questions;
    }
}