package com.example.aienglishtrainer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 메모리 기반 대화 세션 (추후 DB로 이전 예정)
 */
@Getter
@AllArgsConstructor
@Builder
public class ConversationSession {

    private String sessionId;
    private String userId;
    private List<ChatMessage> messages;
    private LocalDateTime startedAt;
    private LocalDateTime lastActivity;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ChatMessage {
        private String role;     // "user", "assistant", "system"
        private String content;
    }

    // 새 세션 생성
    public static ConversationSession createNew(String sessionId, String userId) {
        return ConversationSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .messages(new ArrayList<>())
                .startedAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();
    }

    // 메시지 추가
    public void addMessage(String role, String content) {
        this.messages.add(
                ChatMessage.builder()
                        .role(role)
                        .content(content)
                        .build()
        );
        this.lastActivity = LocalDateTime.now();
    }

    // 마지막 활동 시간 업데이트
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    // 최근 N개 메시지만 반환 (토큰 절약)
    public List<ChatMessage> getRecentMessages(int count) {
        int size = messages.size();
        if (size <= count) {
            return new ArrayList<>(messages);
        }
        return new ArrayList<>(messages.subList(size - count, size));
    }
}