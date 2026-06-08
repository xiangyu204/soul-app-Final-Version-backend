package com.example.soul.service;

import com.example.soul.dto.quiz.*;
import com.example.soul.entity.User;
import com.example.soul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

// 声明这是一个 Service 类，交给 Spring 管理
@Service
public class QuizService {

    // 临时保存测试题目
    // key 是 quizId，value 是这一套测试题
    // 目前先存在内存中，后续正式版可以改成数据库保存
    private final Map<Long, List<QuizQuestionData>> quizStore = new HashMap<>();

    // 用来生成测试编号
    // 每开始一次测试，quizIdSeq 就加 1
    private long quizIdSeq = 1;

    // 用来调用 Ollama API
    private final RestTemplate restTemplate = new RestTemplate();

    // 用来解析 AI 返回的 JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 用户数据库
    private final UserRepository userRepository;

    // 构造方法注入 UserRepository
    public QuizService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 从 application.yml 读取 Ollama 地址
    // 例如：http://localhost:11434
    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    // 从 application.yml 读取模型名
    // 例如：gemma4:31b-cloud
    @Value("${ollama.model}")
    private String ollamaModel;

    // 开始测试
    // 前端调用 /api/quiz/start 时，会执行这个方法
    public QuizStartResponse startQuiz(QuizStartRequest request) {
        System.out.println("QuizService.startQuiz()");

        // 生成一个新的测试编号
        Long quizId = quizIdSeq++;

        // 使用 Ollama AI 根据用户输入的技能生成 20 道题
        List<QuizQuestionData> questions = generateQuestionsByAi(request.getSkill());

        // 把这次测试的题目保存起来
        // 后面提交答案时，要根据 quizId 找回这些题目和正确答案
        quizStore.put(quizId, questions);

        // 转换成返回给前端的数据
        // 注意：这里只返回 questionId、题目、选项
        // 不返回 correctAnswer，防止前端看到正确答案
        List<QuizQuestionResponse> responseQuestions = questions.stream()
                .map(q -> new QuizQuestionResponse(
                        q.getQuestionId(),
                        q.getQuestion(),
                        q.getOptions()
                ))
                .toList();

        // 返回给前端
        return new QuizStartResponse(
                quizId,
                request.getSkill(),
                request.getQuizType(),
                responseQuestions
        );
    }

    // 提交测试答案
    // 前端调用 /api/quiz/submit 时，会执行这个方法
    public QuizSubmitResponse submitQuiz(QuizSubmitRequest request) {

        // 根据 quizId 找到之前生成的题目
        List<QuizQuestionData> questions = quizStore.get(request.getQuizId());

        // 如果找不到，说明测试记录不存在
        if (questions == null) {
            throw new RuntimeException("测试记录不存在");
        }

        // 用 Map 保存用户提交的答案
        // key 是 questionId，value 是用户选择的答案
        Map<Long, String> answerMap = new HashMap<>();

        // 把前端提交的答案放进 answerMap
        if (request.getAnswers() != null) {
            for (QuizAnswerRequest answer : request.getAnswers()) {
                answerMap.put(answer.getQuestionId(), answer.getAnswer());
            }
        }

        // 记录得分
        int score = 0;

        // 遍历所有题目，对比用户答案和正确答案
        for (QuizQuestionData question : questions) {

            // 取出用户对当前题目的答案
            String userAnswer = answerMap.get(question.getQuestionId());

            // 如果用户答案和正确答案相同，分数 +1
            if (Objects.equals(question.getCorrectAnswer(), userAnswer)) {
                score++;
            }
        }

        // 根据分数判断等级
        String level = calculateLevel(score);

        // 把等级英文转换成中文 + 英文显示文本
        String levelText = getLevelText(level);

        // 保存 Quiz 分数和等级到用户表
        saveQuizResult(request, score, level);

        // 提交完成后删除临时题目，避免一直占内存
        quizStore.remove(request.getQuizId());

        // 返回测试结果给前端
        return new QuizSubmitResponse(score, questions.size(), level, levelText);
    }

    // 保存 Quiz 分数和等级到 User 表
    private void saveQuizResult(QuizSubmitRequest request, int score, String level) {

        // username 必须存在，不然不知道保存给哪个用户
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("username 不能为空，无法保存 Quiz 分数");
        }

        // quizType 必须存在，用来判断保存到 teach 还是 learn
        if (request.getQuizType() == null || request.getQuizType().isBlank()) {
            throw new RuntimeException("quizType 不能为空，无法判断测试类型");
        }

        // 根据 username 找到当前用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在：" + request.getUsername()));

        String quizType = request.getQuizType().trim().toLowerCase();

        // teach：保存到我会的技能分数和等级
        if ("teach".equals(quizType)) {
            user.setTeachQuizScore(score);
            user.setSkillOfferLevel(level);
        }

        // learn：保存到我想学的技能分数和等级
        else if ("learn".equals(quizType)) {
            user.setLearnQuizScore(score);
            user.setSkillWantLevel(level);
        }

        // 其他类型不接受
        else {
            throw new RuntimeException("未知的 quizType：" + request.getQuizType());
        }

        // 保存到数据库
        userRepository.save(user);
    }

    // 调用 Ollama AI 生成 20 道题
    private List<QuizQuestionData> generateQuestionsByAi(String skill) {

        // 给 AI 的提示词
        // 要求 AI 必须返回 JSON 数组，方便后端解析
        String prompt = """
                你是一个技能测试题生成器。

                请根据用户输入的技能生成 20 道选择题。

                要求：
                1. 题目从入门到高级逐渐增加难度
                2. 每道题有 4 个选项
                3. 每道题只有一个正确答案
                4. correctAnswer 只能是 A、B、C、D
                5. 必须返回 JSON 数组
                6. 不要返回解释文字
                7. 不要使用 markdown
                8. 不要使用 ```json
                9. 题目语言使用中文
                10. 选项不要带 A、B、C、D 前缀，只返回选项文字

                用户输入的技能是：%s

                返回格式必须严格如下：
                [
                  {
                    "question": "题目内容",
                    "options": ["选项A", "选项B", "选项C", "选项D"],
                    "correctAnswer": "A"
                  }
                ]
                """.formatted(skill);

        // Ollama /api/generate 接口需要的请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", ollamaModel);
        body.put("prompt", prompt);
        body.put("stream", false);

        System.out.println("🔥 调用 Ollama AI 生成 Quiz 题目");
        System.out.println("模型：" + ollamaModel);
        System.out.println("技能：" + skill);

        // 调用 Ollama API
        Map response = restTemplate.postForObject(
                ollamaBaseUrl + "/api/generate",
                body,
                Map.class
        );

        // 判断 AI 是否有返回内容
        if (response == null || response.get("response") == null) {
            throw new RuntimeException("AI 没有返回题目");
        }

        // Ollama 返回的真正文本内容在 response 字段里
        String aiText = response.get("response").toString();

        System.out.println("AI 原始返回内容：");
        System.out.println(aiText);

        // 清理 AI 可能返回的 ```json 代码块或多余文字
        String jsonText = cleanJson(aiText);

        try {
            // 把 AI 返回的 JSON 字符串解析成 Java 对象
            List<AiQuestion> aiQuestions = objectMapper.readValue(
                    jsonText,
                    new TypeReference<List<AiQuestion>>() {}
            );

            // 判断题目数量是否足够
            if (aiQuestions.size() < 20) {
                throw new RuntimeException("AI 生成的题目不足 20 道");
            }

            List<QuizQuestionData> result = new ArrayList<>();

            // 转换成 QuizService 内部使用的题目结构
            for (int i = 0; i < 20; i++) {
                AiQuestion aiQuestion = aiQuestions.get(i);

                // 检查题目内容
                if (aiQuestion.getQuestion() == null || aiQuestion.getQuestion().isBlank()) {
                    throw new RuntimeException("第 " + (i + 1) + " 道题内容为空");
                }

                // 检查选项是否存在
                if (aiQuestion.getOptions() == null || aiQuestion.getOptions().size() < 4) {
                    throw new RuntimeException("第 " + (i + 1) + " 道题选项不足 4 个");
                }

                // AI 返回的 correctAnswer 是 A/B/C/D
                // 但是前端提交的是选项文字
                // 所以这里要把 A/B/C/D 转换成真正的选项文字
                String correctAnswerText = convertCorrectAnswerToText(
                        aiQuestion.getCorrectAnswer(),
                        aiQuestion.getOptions()
                );

                result.add(new QuizQuestionData(
                        (long) (i + 1),
                        aiQuestion.getQuestion(),
                        aiQuestion.getOptions(),
                        correctAnswerText
                ));
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("AI 题目解析失败：" + e.getMessage());
        }
    }

    // 清理 AI 返回内容
    // 有时候 AI 会返回 ```json ... ``` 或者前后加解释文字
    private String cleanJson(String text) {
        String result = text
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // 只截取 JSON 数组部分
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");

        if (start != -1 && end != -1 && end > start) {
            result = result.substring(start, end + 1);
        }

        return result;
    }

    // 把 AI 返回的 A/B/C/D 转换成选项文字
    private String convertCorrectAnswerToText(String correctAnswer, List<String> options) {
        String answer = correctAnswer == null ? "" : correctAnswer.trim().toUpperCase();

        // 防止 AI 返回 "A."、"A、"、"A:xxx" 这种格式
        if (answer.startsWith("A")) {
            return options.get(0);
        } else if (answer.startsWith("B")) {
            return options.get(1);
        } else if (answer.startsWith("C")) {
            return options.get(2);
        } else if (answer.startsWith("D")) {
            return options.get(3);
        }

        // 如果 AI 直接返回了选项文字，就原样保存
        return correctAnswer;
    }

    // 根据分数计算等级
    // 总分是 20 分
    private String calculateLevel(int score) {
        if (score <= 5) {
            return "beginner";
        } else if (score <= 10) {
            return "basic";
        } else if (score <= 15) {
            return "intermediate";
        } else {
            return "advanced";
        }
    }

    // 把等级代码转换成前端显示用的文字
    private String getLevelText(String level) {
        return switch (level) {
            case "beginner" -> "入门 Beginner";
            case "basic" -> "基础 Basic";
            case "intermediate" -> "中级 Intermediate";
            case "advanced" -> "进阶 Advanced";
            default -> "未知等级";
        };
    }

    // 内部类：表示一道测试题的数据
    // 这个类只在 QuizService 内部使用
    static class QuizQuestionData {

        // 题目编号
        private Long questionId;

        // 题目内容
        private String question;

        // 选项列表
        private List<String> options;

        // 正确答案
        // 注意：这个字段不能返回给前端
        private String correctAnswer;

        // 构造方法：创建题目对象时使用
        public QuizQuestionData(Long questionId, String question, List<String> options, String correctAnswer) {
            this.questionId = questionId;
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
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

    // 内部类：接收 AI 返回的题目结构
    // AI 返回 JSON 后，会先转换成这个类
    static class AiQuestion {

        // 题目内容
        private String question;

        // 四个选项
        private List<String> options;

        // 正确答案，格式是 A/B/C/D
        private String correctAnswer;

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }
    }
}