package com.example.soul.dto.quiz;

import java.util.List;

// 返回给前端的一道题
public class QuizQuestionResponse {

    // 题目编号
    private Long questionId;

    // 题目内容
    private String question;

    // 选项列表
    private List<String> options;

    public QuizQuestionResponse(Long questionId, String question, List<String> options) {
        this.questionId = questionId;
        this.question = question;
        this.options = options;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }
}