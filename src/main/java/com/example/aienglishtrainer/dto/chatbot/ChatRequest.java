package com.example.aienglishtrainer.dto.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    @NotBlank(message = "메시지는 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    private String message;

    private String conversationId;  // null이면 새 대화 생성
}