package com.example.soul.service;

import com.example.soul.dto.AiSuggestRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:gemma4:e4b}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    // 새로 추가：AI 학습 도움 / 지식 설명 기능
    public String explainKnowledge(AiSuggestRequest request) {

        String userQuestion = request.getMessage();
        String chatContext = request.getContext();

        String targetLang = request.getTargetLang();

        // 默认韩语
        if (targetLang == null || targetLang.isBlank()) {
            targetLang = "ko";
        }

        // AI 回复语言
        String answerLanguage;

        switch (targetLang.toLowerCase()) {

            case "zh":
            case "zh-cn":
                answerLanguage = "中文";
                break;

            case "en":
                answerLanguage = "English";
                break;

            case "ja":
                answerLanguage = "日本語";
                break;

            case "fr":
                answerLanguage = "Français";
                break;

            case "de":
                answerLanguage = "Deutsch";
                break;

            case "es":
                answerLanguage = "Español";
                break;

            case "ar":
                answerLanguage = "العربية";
                break;

            default:
                answerLanguage = "한국어";
        }
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            userQuestion = "최근 채팅 내용에서 사용자가 이해하지 못한 지식 포인트를 찾아서 설명해주세요.";
        }

        if (chatContext == null || chatContext.trim().isEmpty()) {
            chatContext = "최근 채팅 내용이 없습니다.";
        }

        String prompt = """
                당신은 SOUL 기술 교환 플랫폼의 AI 학습 도우미입니다.

                두 사용자가 채팅하면서 특정 지식이나 기술 개념을 이해하지 못하고 있습니다.
                아래 사용자의 질문과 최근 채팅 내용을 바탕으로, 해당 지식 포인트를 쉽게 설명하세요.

                답변 조건:
                1. 반드시 """
                + answerLanguage +
                """ 
                로 답변하세요.
                2. 초보자도 이해할 수 있게 쉽게 설명하세요.
                3. 가능하면 간단한 예시를 포함하세요.
                4. 너무 길게 쓰지 말고 3~5문장으로 설명하세요.
                5. 답장 추천이 아니라, 지식 설명을 하세요.
                6. 채팅창에 바로 표시될 AI 답변만 출력하세요.
                7. "제가 대신 답장해드릴게요" 같은 표현은 쓰지 마세요.

                사용자의 질문:
                """ + userQuestion + """

                최근 채팅 내용:
                """ + chatContext;

        return callOllama(
                prompt,
                "당신은 기술 교환 플랫폼의 AI 학습 도우미입니다. 사용자가 모르는 개념을 쉽게 설명합니다."
        );
    }

    // Ollama 호출 공통 메서드
    private String callOllama(String prompt, String systemMessage) {

        Map<String, Object> body = Map.of(
                "model", ollamaModel,
                "stream", false,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemMessage
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        Map response = restTemplate.postForObject(
                ollamaBaseUrl + "/api/chat",
                body,
                Map.class
        );

        if (response == null || response.get("message") == null) {
            return "AI가 답변을 생성하지 못했습니다. 다시 질문해 주세요.";
        }

        Map message = (Map) response.get("message");

        Object content = message.get("content");

        if (content == null) {
            return "AI가 답변을 생성하지 못했습니다. 다시 질문해 주세요.";
        }

        return content.toString().trim();
    }
}