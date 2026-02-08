package com.example.aienglishtrainer.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ConversationHistoryResponse {

    private String conversationId;
    private List<MessageDto> messages;
    private LocalDateTime startedAt;
    private LocalDateTime lastActivity;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MessageDto {
        private String role;     // "user" or "assistant"
        private String content;
    }
}