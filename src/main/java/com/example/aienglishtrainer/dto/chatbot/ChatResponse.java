package com.example.aienglishtrainer.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatResponse {

    private String message;          // AI 응답 메시지
    private String conversationId;   // 대화 세션 ID
    private List<String> suggestions; // 추천 질문 (선택)
    private LocalDateTime timestamp;
}