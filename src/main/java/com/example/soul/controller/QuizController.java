package com.example.soul.controller;

import com.example.soul.dto.quiz.QuizStartRequest;
import com.example.soul.dto.quiz.QuizStartResponse;
import com.example.soul.dto.quiz.QuizSubmitRequest;
import com.example.soul.dto.quiz.QuizSubmitResponse;
import com.example.soul.service.QuizService;
import org.springframework.web.bind.annotation.*;

// Quiz 测试接口控制器
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    // 构造方法注入 QuizService
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // 开始测试接口
    // 前端请求地址：POST /api/quiz/start
    @PostMapping("/start")
    public QuizStartResponse startQuiz(@RequestBody QuizStartRequest request) {
        System.out.println("========== QUIZ START ==========");
        System.out.println("skill = " + request.getSkill());
        System.out.println("quizType = " + request.getQuizType());

        return quizService.startQuiz(request);
    }

    // 提交测试接口
    // 前端请求地址：POST /api/quiz/submit
    @PostMapping("/submit")
    public QuizSubmitResponse submitQuiz(@RequestBody QuizSubmitRequest request) {
        System.out.println("========== QUIZ SUBMIT ==========");
        System.out.println("username = " + request.getUsername());
        System.out.println("skill = " + request.getSkill());
        System.out.println("quizType = " + request.getQuizType());

        return quizService.submitQuiz(request);
    }
}