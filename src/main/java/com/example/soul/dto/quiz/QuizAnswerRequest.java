package com.example.soul.dto.quiz;

// 前端提交的一道题答案
public class QuizAnswerRequest {

    // 题目编号
    private Long questionId;

    // 用户选择的答案
    private String answer;

    public Long getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}