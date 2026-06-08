package com.example.soul.dto.quiz;

// 测试提交后返回给前端的结果
public class QuizSubmitResponse {

    // 得分
    private int score;

    // 总题数
    private int total;

    // 等级代码 beginner / basic / intermediate / advanced
    private String level;

    // 前端显示用的等级文字
    private String levelText;

    public QuizSubmitResponse(int score, int total, String level, String levelText) {
        this.score = score;
        this.total = total;
        this.level = level;
        this.levelText = levelText;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public String getLevel() {
        return level;
    }

    public String getLevelText() {
        return levelText;
    }
}