package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.chatbot.ChatResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationHistoryResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationListResponse;
import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.security.SecurityUtil;
import com.example.aienglishtrainer.service.ChatbotService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    // 메시지 전송
    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @RequestBody ChatMessageRequest request) {

        // 메시지 유효성 검증
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Message is required"));
        }
        if (request.getMessage().length() > 1000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Message is too long (max 1000 characters)"));
        }

        String userId = SecurityUtil.getCurrentUsername();
        ChatResponse response = chatbotService.sendMessage(
                userId, request.getMessage(), request.getConversationId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 대화 목록 조회
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationListResponse>>> getConversations() {
        String userId = SecurityUtil.getCurrentUsername();
        List<ConversationListResponse> conversations = chatbotService.getUserConversations(userId);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    // 대화 히스토리 조회
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationHistoryResponse>> getHistory(
            @PathVariable String conversationId) {
        ConversationHistoryResponse history = chatbotService.getConversationHistory(conversationId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    // 대화 삭제
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable String conversationId) {
        chatbotService.deleteConversation(conversationId);
        return ResponseEntity.ok(ApiResponse.success("대화가 삭제되었습니다.", null));
    }

    @Data
    static class ChatMessageRequest {
        private String message;
        private String conversationId;
    }
}