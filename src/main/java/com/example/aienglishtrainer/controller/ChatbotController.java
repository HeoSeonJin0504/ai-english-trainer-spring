package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.chatbot.ChatRequest;
import com.example.aienglishtrainer.dto.chatbot.ChatResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationHistoryResponse;
import com.example.aienglishtrainer.dto.chatbot.ConversationListResponse;
import com.example.aienglishtrainer.security.SecurityUtil;
import com.example.aienglishtrainer.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * 챗봇에게 메시지 전송
     * POST /api/chat/message
     */
    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @Valid @RequestBody ChatRequest request) {

        // 현재 로그인한 사용자 ID
        Long userId = SecurityUtil.getCurrentUserId();

        ChatResponse response = chatbotService.sendMessage(
                userId.toString(),
                request.getMessage(),
                request.getConversationId()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자의 모든 대화 목록 조회
     * GET /api/chat/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationListResponse>>> getConversations() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<ConversationListResponse> conversations =
                chatbotService.getUserConversations(userId.toString());

        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    /**
     * 특정 대화 히스토리 조회
     * GET /api/chat/history/{conversationId}
     */
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationHistoryResponse>> getHistory(
            @PathVariable String conversationId) {

        ConversationHistoryResponse history =
                chatbotService.getConversationHistory(conversationId);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 대화 삭제
     * DELETE /api/chat/{conversationId}
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable String conversationId) {

        chatbotService.deleteConversation(conversationId);

        return ResponseEntity.ok(ApiResponse.success("대화가 삭제되었습니다."));
    }
}