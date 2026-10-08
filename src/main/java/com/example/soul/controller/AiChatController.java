package com.example.soul.controller;

import com.example.soul.entity.User;
import com.example.soul.repository.UserRepository;
import com.example.soul.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiChatController {

    private final UserRepository userRepository;
    private final TranslateService translateService;

    @Autowired
    public AiChatController(UserRepository userRepository, TranslateService translateService) {
        this.userRepository = userRepository;
        this.translateService = translateService;
    }

    /**
     * 1. 星轨 AI 灵魂导游接口 — 【已全面赋能跨语言同步翻译】
     */
    @GetMapping("/guide")
    public ResponseEntity<Map<String, String>> getAiGuide(
            @RequestParam String username,
            @RequestParam(required = false, defaultValue = "CHINESE") String targetLang) {

        Map<String, String> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("advice", "未找到该空间维度的用户，请先完善个人信息。");
            return ResponseEntity.ok(response);
        }

        String teach = user.getSkillOffer() != null ? user.getSkillOffer() : "未填写";
        String want = user.getSkillWant() != null ? user.getSkillWant() : "未填写";

        String prompt = String.format("当前用户的可传授技能是[%s]，想学习的技能是[%s]。", teach, want);

        // 核心亮点：先随机抽取一条干货建议
        String aiReply = callLargeLanguageModel(prompt);

        // 🚀 核心重构：根据前端当前所选语言，自动调用您的 Ollama 翻译大模型！
        if (!"CHINESE".equalsIgnoreCase(targetLang)) {
            String langName = "English";
            if ("KOREAN".equalsIgnoreCase(targetLang)) langName = "Korean";

            try {
                // 利用您编写的高性能断句大模型服务进行二次量子同频翻译
                aiReply = translateService.translate(aiReply, langName);
            } catch (Exception e) {
                System.err.println("AI导游运势翻译失败: " + e.getMessage());
            }
        }

        response.put("advice", aiReply);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 全局 AI 实时翻译接口
     */
    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translateText(@RequestParam String text, @RequestParam String targetLang) {
        Map<String, String> response = new HashMap<>();

        String langName = targetLang;
        if ("CHINESE".equalsIgnoreCase(targetLang)) langName = "Chinese";
        else if ("KOREAN".equalsIgnoreCase(targetLang)) langName = "Korean";
        else if ("ENGLISH".equalsIgnoreCase(targetLang)) langName = "English";

        try {
            String rawResult = translateService.translate(text, langName);
            String cleanedResult = rawResult
                    .replaceAll("(?i)(Here is the translation|Translation|Here's the translation)[:：\\s]*", "")
                    .replaceAll("(?i)(翻译结果如下|翻译|以下是翻译)[:：\\s]*", "")
                    .trim();
            response.put("advice", cleanedResult);
        } catch (Exception e) {
            response.put("advice", "[量子纠缠通讯超时]");
        }

        return ResponseEntity.ok(response);
    }

    private String callLargeLanguageModel(String prompt) {
        System.out.println("[星轨 AI 灵魂导游] 提示词长度: " + prompt.length());

        String[] pureAdvices = {
                "当前知识输出场极度充沛！今日适合向同行者展示你在专业领域的积淀，主动分享将为你吸引来高共鸣度的契合灵魂。",
                "思维频率正处于高频接收状态，吸收新事物的效率提升。今日宜向那些身怀绝技的Souler发起连线，打破固有瓶颈。",
                "检测到今日有数个高契合度的引力源在您的星轨外交汇。不必等待，主动抛出第一颗星愿，共鸣会带给你满意的答案。"
        };

        return pureAdvices[new Random().nextInt(pureAdvices.length)];
    }
}