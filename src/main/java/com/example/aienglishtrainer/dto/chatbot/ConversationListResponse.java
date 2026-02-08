package com.example.aienglishtrainer.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ConversationListResponse {

    private String conversationId;
    private String preview;         // 첫 메시지 미리보기
    private int messageCount;
    private LocalDateTime startedAt;
    private LocalDateTime lastActivity;
}