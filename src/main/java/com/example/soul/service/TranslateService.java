package com.example.soul.service;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslateService {

    private final RestTemplate restTemplate;

    public TranslateService() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(30000);
        factory.setReadTimeout(300000);

        this.restTemplate = new RestTemplate(factory);
    }

    @Cacheable(
            value = "translateCache",
            key = "#text.trim().toLowerCase() + '_' + #targetLang.toLowerCase()"
    )
    public String translate(String text, String targetLang) {

        System.out.println("🔥 调用了 Ollama 翻译");

        // 长文本分段
        String[] parts = text.split("(?<=。|！|？|\\.|!|\\?)");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {

            if (part.isBlank()) {
                continue;
            }

            result.append(
                    translateSingle(part, targetLang)
            ).append(" ");
        }

        return result.toString().trim();
    }

    private String translateSingle(String text, String targetLang) {

        String url = "http://localhost:11434/api/generate";

        //  强化 Prompt
        String prompt =
                "You are a professional translator.\n" +
                        "Translate the following text into " + targetLang + ".\n" +
                        "ONLY return translated text.\n" +
                        "Do NOT explain anything.\n\n" +
                        "Text:\n" + text;

        Map<String, Object> body = new HashMap<>();

        body.put("model", "translategemma:4b");
        body.put("prompt", prompt);
        body.put("stream", false);

        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.2);
        options.put("num_predict", 300);

        body.put("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Object translated = response.getBody().get("response");

        return translated == null ? text : translated.toString().trim();
    }

    // 模型预热
    @PostConstruct
    public void warmUp() {

        try {

            translate("你好", "English");

            System.out.println("translategemma:4b 已预热");

        } catch (Exception e) {

            System.out.println("模型预热失败");
        }
    }
}