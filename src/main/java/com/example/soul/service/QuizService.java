package com.example.soul.service;

import com.example.soul.dto.quiz.*;
import com.example.soul.entity.User;
import com.example.soul.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class QuizService {

    // =========================================================
    // Quiz 临时存储
    // =========================================================

    private final Map<Long, List<QuizQuestionData>> quizStore =
            new HashMap<>();

    private long quizIdSeq = 1;


    // =========================================================
    // Dependency
    // =========================================================

    private final RestTemplate restTemplate =
            new RestTemplate();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final UserRepository userRepository;


    public QuizService(
            UserRepository userRepository
    ) {
        this.userRepository =
                userRepository;
    }


    // =========================================================
    // DeepSeek 配置
    // =========================================================

    @Value("${deepseek.base-url}")
    private String deepseekBaseUrl;


    @Value("${deepseek.api-key}")
    private String deepseekApiKey;


    @Value("${deepseek.model}")
    private String deepseekModel;


    // =========================================================
    // 开始 Quiz
    // =========================================================

    public QuizStartResponse startQuiz(
            QuizStartRequest request
    ) {

        System.out.println(
                "========== QuizService.startQuiz =========="
        );

        System.out.println(
                "skill = " + request.getSkill()
        );

        System.out.println(
                "quizType = " + request.getQuizType()
        );

        System.out.println(
                "targetLang = " + request.getTargetLang()
        );

        System.out.println(
                "username = " + request.getUsername()
        );


        Long quizId =
                quizIdSeq++;


        // =====================================================
        // DeepSeek 生成 5 道题
        // =====================================================

        List<QuizQuestionData> questions =
                generateQuestionsByAi(
                        request.getSkill(),
                        request.getTargetLang()
                );


        // =====================================================
        // 保存当前 Quiz
        // =====================================================

        quizStore.put(
                quizId,
                questions
        );


        // =====================================================
        // 转成前端 DTO
        // =====================================================

        List<QuizQuestionResponse> responseQuestions =
                questions
                        .stream()
                        .map(
                                q ->
                                        new QuizQuestionResponse(
                                                q.getQuestionId(),
                                                q.getQuestion(),
                                                q.getOptions()
                                        )
                        )
                        .toList();


        return new QuizStartResponse(
                quizId,
                request.getSkill(),
                request.getQuizType(),
                responseQuestions
        );
    }


    // =========================================================
    // 提交 Quiz
    // =========================================================

    public QuizSubmitResponse submitQuiz(
            QuizSubmitRequest request
    ) {

        System.out.println(
                "========== QuizService.submitQuiz =========="
        );


        List<QuizQuestionData> questions =
                quizStore.get(
                        request.getQuizId()
                );


        if (questions == null) {

            throw new RuntimeException(
                    "测试记录不存在"
            );
        }


        // =====================================================
        // 用户答案
        // =====================================================

        Map<Long, String> answerMap =
                new HashMap<>();


        if (
                request.getAnswers()
                        != null
        ) {

            for (
                    QuizAnswerRequest answer
                    :
                    request.getAnswers()
            ) {

                answerMap.put(
                        answer.getQuestionId(),
                        answer.getAnswer()
                );
            }
        }


        // =====================================================
        // 评分
        // =====================================================

        int score = 0;


        for (
                QuizQuestionData question
                :
                questions
        ) {

            String userAnswer =
                    answerMap.get(
                            question.getQuestionId()
                    );


            if (
                    Objects.equals(
                            question.getCorrectAnswer(),
                            userAnswer
                    )
            ) {

                score++;
            }
        }


        String level =
                calculateLevel(
                        score,
                        questions.size()
                );


        String levelText =
                getLevelText(
                        level
                );


        // =====================================================
        // 保存数据库
        // =====================================================

        saveQuizResult(
                request,
                score,
                level
        );


        quizStore.remove(
                request.getQuizId()
        );


        return new QuizSubmitResponse(
                score,
                questions.size(),
                level,
                levelText
        );
    }


    // =========================================================
    // 保存 Quiz 结果
    // =========================================================

    private void saveQuizResult(
            QuizSubmitRequest request,
            int score,
            String level
    ) {

        if (
                request.getUsername()
                        == null
                        ||
                        request.getUsername()
                                .isBlank()
        ) {

            throw new RuntimeException(
                    "username 不能为空，无法保存 Quiz 分数"
            );
        }


        if (
                request.getQuizType()
                        == null
                        ||
                        request.getQuizType()
                                .isBlank()
        ) {

            throw new RuntimeException(
                    "quizType 不能为空"
            );
        }


        User user =
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "用户不存在："
                                                        +
                                                        request.getUsername()
                                        )
                        );


        String quizType =
                request
                        .getQuizType()
                        .trim()
                        .toLowerCase();


        if (
                "teach".equals(
                        quizType
                )
        ) {

            user.setTeachQuizScore(
                    score
            );

            user.setSkillOfferLevel(
                    level
            );

        } else if (
                "learn".equals(
                        quizType
                )
        ) {

            user.setLearnQuizScore(
                    score
            );

            user.setSkillWantLevel(
                    level
            );

        } else {

            throw new RuntimeException(
                    "未知的 quizType："
                            +
                            request.getQuizType()
            );
        }


        userRepository.save(
                user
        );
    }


    // =========================================================
    // DeepSeek AI 生成题目
    // =========================================================

    private List<QuizQuestionData> generateQuestionsByAi(
            String skill,
            String targetLang
    ) {

        // =====================================================
        // Language
        // =====================================================

        String questionLanguage;


        if (
                targetLang == null
                        ||
                        targetLang.isBlank()
        ) {

            targetLang =
                    "ko";
        }


        switch (
                targetLang.toLowerCase()
        ) {

            case "zh":
            case "zh-cn":

                questionLanguage =
                        "中文";

                break;


            case "en":

                questionLanguage =
                        "English";

                break;


            case "ja":

                questionLanguage =
                        "日本語";

                break;


            default:

                questionLanguage =
                        "한국어";
        }


        // =====================================================
        // Prompt
        // =====================================================

        String prompt = """
                Create exactly 5 concise multiple-choice questions about "%s".

                Language: %s

                Requirements:
                - Exactly 5 questions
                - Exactly 4 options per question
                - One correct answer
                - correctAnswer must exactly equal one option
                - Keep text concise
                - No explanations

                Return ONLY this JSON object:

                {
                  "questions": [
                    {
                      "question": "...",
                      "options": [
                        "...",
                        "...",
                        "...",
                        "..."
                      ],
                      "correctAnswer": "..."
                    }
                  ]
                }
                """.formatted(
                skill,
                questionLanguage
        );


        // =====================================================
        // messages
        // =====================================================

        List<Map<String, Object>> messages =
                new ArrayList<>();


        messages.add(
                Map.of(
                        "role",
                        "system",

                        "content",
                        "You generate concise assessment quizzes. Output valid JSON only."
                )
        );


        messages.add(
                Map.of(
                        "role",
                        "user",

                        "content",
                        prompt
                )
        );


        // =====================================================
        // DeepSeek Request Body
        // =====================================================

        Map<String, Object> body =
                new HashMap<>();


        body.put(
                "model",
                deepseekModel
        );


        body.put(
                "messages",
                messages
        );


        // 不使用流式
        body.put(
                "stream",
                false
        );


        // =====================================================
        // 关闭 thinking
        // 追求速度
        // =====================================================

        body.put(
                "thinking",
                Map.of(
                        "type",
                        "disabled"
                )
        );


        // =====================================================
        // JSON Output
        // =====================================================

        body.put(
                "response_format",
                Map.of(
                        "type",
                        "json_object"
                )
        );


        // =====================================================
        // 限制输出长度
        // =====================================================

        body.put(
                "max_tokens",
                1000
        );


        // =====================================================
        // Headers
        // =====================================================

        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_JSON
        );


        headers.setBearerAuth(
                deepseekApiKey
        );


        HttpEntity<
                Map<String, Object>
                > entity =
                new HttpEntity<>(
                        body,
                        headers
                );


        // =====================================================
        // Call DeepSeek
        // =====================================================

        long startTime =
                System.currentTimeMillis();


        try {

            System.out.println(
                    "🔥 调用 DeepSeek 生成 Quiz"
            );

            System.out.println(
                    "model = "
                            +
                            deepseekModel
            );


            ResponseEntity<Map> response =
                    restTemplate.postForEntity(

                            deepseekBaseUrl
                                    +
                                    "/chat/completions",

                            entity,

                            Map.class
                    );


            long endTime =
                    System.currentTimeMillis();


            System.out.println(
                    "⏱ DeepSeek 生成耗时："
                            +
                            (
                                    endTime
                                            -
                                            startTime
                            )
                                    /
                                    1000.0
                            +
                            " 秒"
            );


            Map responseBody =
                    response.getBody();


            if (
                    responseBody == null
            ) {

                System.err.println(
                        "DeepSeek response body 为空"
                );

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            // =================================================
            // choices
            // =================================================

            Object choicesObject =
                    responseBody.get(
                            "choices"
                    );


            if (
                    !(choicesObject
                            instanceof List<?> choices)
                            ||
                            choices.isEmpty()
            ) {

                System.err.println(
                        "DeepSeek choices 为空"
                );

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            Object firstChoice =
                    choices.get(
                            0
                    );


            if (
                    !(firstChoice
                            instanceof Map<?, ?> choiceMap)
            ) {

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            Object messageObject =
                    choiceMap.get(
                            "message"
                    );


            if (
                    !(messageObject
                            instanceof Map<?, ?> messageMap)
            ) {

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            Object contentObject =
                    messageMap.get(
                            "content"
                    );


            if (
                    contentObject == null
            ) {

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            String aiText =
                    contentObject.toString();


            System.out.println(
                    "DeepSeek AI返回："
            );

            System.out.println(
                    aiText
            );


            // =================================================
            // JSON -> Wrapper
            // =================================================

            AiQuizResponse parsed =
                    objectMapper.readValue(
                            aiText,
                            AiQuizResponse.class
                    );


            if (
                    parsed == null
                            ||
                            parsed.getQuestions()
                                    == null
                            ||
                            parsed.getQuestions()
                                    .isEmpty()
            ) {

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            // =================================================
            // 转换题目
            // =================================================

            List<QuizQuestionData> result =
                    new ArrayList<>();


            for (
                    AiQuestion aiQuestion
                    :
                    parsed.getQuestions()
            ) {


                if (
                        result.size()
                                >=
                                5
                ) {

                    break;
                }


                if (
                        aiQuestion.getQuestion()
                                == null
                                ||
                                aiQuestion.getQuestion()
                                        .isBlank()
                ) {

                    continue;
                }


                if (
                        aiQuestion.getOptions()
                                == null
                                ||
                                aiQuestion.getOptions()
                                        .size()
                                        <
                                        4
                ) {

                    continue;
                }


                List<String> options =
                        new ArrayList<>(
                                aiQuestion
                                        .getOptions()
                                        .subList(
                                                0,
                                                4
                                        )
                        );


                String correctAnswer =
                        convertCorrectAnswerToText(

                                aiQuestion
                                        .getCorrectAnswer(),

                                options
                        );


                result.add(

                        new QuizQuestionData(

                                (long)
                                        (
                                                result.size()
                                                        +
                                                        1
                                        ),

                                aiQuestion
                                        .getQuestion(),

                                options,

                                correctAnswer
                        )
                );
            }


            if (
                    result.size()
                            !=
                            5
            ) {

                System.err.println(
                        "AI 有效题目不足5道"
                );

                return getDefaultFallbackQuestions(
                        skill
                );
            }


            System.out.println(
                    "✅ DeepSeek成功生成5道题"
            );


            return result;


        } catch (Exception e) {

            System.err.println(
                    "❌ DeepSeek API请求失败："
                            +
                            e.getMessage()
            );


            e.printStackTrace();


            return getDefaultFallbackQuestions(
                    skill
            );
        }
    }


    // =========================================================
    // Fallback
    // =========================================================

    private List<QuizQuestionData> getDefaultFallbackQuestions(
            String skill
    ) {

        List<QuizQuestionData> fallback =
                new ArrayList<>();


        for (
                int i = 1;
                i <= 5;
                i++
        ) {

            fallback.add(

                    new QuizQuestionData(

                            (long) i,

                            "["
                                    +
                                    skill
                                    +
                                    "] Default Assessment Question "
                                    +
                                    i,

                            List.of(
                                    "Option A",
                                    "Option B",
                                    "Option C",
                                    "Option D"
                            ),

                            "Option A"
                    )
            );
        }


        return fallback;
    }


    // =========================================================
    // Correct Answer
    // =========================================================

    private String convertCorrectAnswerToText(
            String correctAnswer,
            List<String> options
    ) {

        if (
                options == null
                        ||
                        options.isEmpty()
        ) {

            return "";
        }


        if (
                correctAnswer == null
                        ||
                        correctAnswer.isBlank()
        ) {

            return options.get(
                    0
            );
        }


        String raw =
                correctAnswer.trim();


        String answer =
                raw.toUpperCase();


        if (
                answer.equals("A")
                        ||
                        answer.equals("1")
        ) {

            return options.get(
                    0
            );
        }


        if (
                answer.equals("B")
                        ||
                        answer.equals("2")
        ) {

            return options.get(
                    1
            );
        }


        if (
                answer.equals("C")
                        ||
                        answer.equals("3")
        ) {

            return options.get(
                    2
            );
        }


        if (
                answer.equals("D")
                        ||
                        answer.equals("4")
        ) {

            return options.get(
                    3
            );
        }


        for (
                String option
                :
                options
        ) {

            if (
                    option.equalsIgnoreCase(
                            raw
                    )
            ) {

                return option;
            }
        }


        return options.get(
                0
        );
    }


    // =========================================================
    // Level
    // =========================================================

    private String calculateLevel(
            int score,
            int totalQuestions
    ) {

        if (
                totalQuestions
                        ==
                        0
        ) {

            return "beginner";
        }


        double percentage =
                (double) score
                        /
                        totalQuestions;


        if (
                percentage
                        <=
                        0.25
        ) {

            return "beginner";

        } else if (
                percentage
                        <=
                        0.50
        ) {

            return "basic";

        } else if (
                percentage
                        <=
                        0.75
        ) {

            return "intermediate";

        } else {

            return "advanced";
        }
    }


    private String getLevelText(
            String level
    ) {

        return switch (
                level
                ) {

            case "beginner" ->
                    "入门 Beginner";

            case "basic" ->
                    "基础 Basic";

            case "intermediate" ->
                    "中级 Intermediate";

            case "advanced" ->
                    "进阶 Advanced";

            default ->
                    "未知等级";
        };
    }


    // =========================================================
    // QuizQuestionData
    // =========================================================

    static class QuizQuestionData {

        private Long questionId;

        private String question;

        private List<String> options;

        private String correctAnswer;


        public QuizQuestionData(
                Long questionId,
                String question,
                List<String> options,
                String correctAnswer
        ) {

            this.questionId =
                    questionId;

            this.question =
                    question;

            this.options =
                    options;

            this.correctAnswer =
                    correctAnswer;
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


        public String getCorrectAnswer() {

            return correctAnswer;
        }
    }


    // =========================================================
    // DeepSeek JSON Wrapper
    // =========================================================

    static class AiQuizResponse {

        private List<AiQuestion> questions;


        public AiQuizResponse() {
        }


        public List<AiQuestion> getQuestions() {

            return questions;
        }


        public void setQuestions(
                List<AiQuestion> questions
        ) {

            this.questions =
                    questions;
        }
    }


    // =========================================================
    // AI Question
    // =========================================================

    static class AiQuestion {

        private String question;

        private List<String> options;

        private String correctAnswer;


        public AiQuestion() {
        }


        public String getQuestion() {

            return question;
        }


        public List<String> getOptions() {

            return options;
        }


        public String getCorrectAnswer() {

            return correctAnswer;
        }


        public void setQuestion(
                String question
        ) {

            this.question =
                    question;
        }


        public void setOptions(
                List<String> options
        ) {

            this.options =
                    options;
        }


        public void setCorrectAnswer(
                String correctAnswer
        ) {

            this.correctAnswer =
                    correctAnswer;
        }
    }
}