package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.openai.GenerateExampleRequest;
import com.example.aienglishtrainer.dto.openai.GenerateExampleResponse;
import com.example.aienglishtrainer.dto.openai.GenerateQuestionRequest;
import com.example.aienglishtrainer.dto.openai.GenerateQuestionResponse;
import com.example.aienglishtrainer.service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final OpenAiService openAiService;

    // 예문 생성
    @PostMapping("/examples")
    public ResponseEntity<ApiResponse<GenerateExampleResponse>> generateExamples(
            @Valid @RequestBody GenerateExampleRequest request) {

        GenerateExampleResponse response = openAiService.generateExamples(request.getWord());

        // 유효하지 않은 단어인 경우
        if (!response.isValid()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(response.getErrorMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 문제 생성
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<GenerateQuestionResponse>> generateQuestions(
            @Valid @RequestBody GenerateQuestionRequest request) {

        GenerateQuestionResponse response = openAiService.generateQuestions(
                request.getTopic(), request.getMode());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}