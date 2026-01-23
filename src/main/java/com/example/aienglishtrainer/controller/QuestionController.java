package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.question.QuestionSaveRequest;
import com.example.aienglishtrainer.dto.question.QuestionResponse;
import com.example.aienglishtrainer.entity.QuestionMode;
import com.example.aienglishtrainer.entity.ToeicPart;
import com.example.aienglishtrainer.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 문제 저장
    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> saveQuestion(
            @Valid @RequestBody QuestionSaveRequest request) {

        QuestionResponse response = questionService.saveQuestion(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("문제가 저장되었습니다.", response));
    }

    // 내 문제 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getMyQuestions() {
        List<QuestionResponse> questions = questionService.getMyQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 토익 문제 조회
    @GetMapping("/toeic")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getToeicQuestions() {
        List<QuestionResponse> questions = questionService.getQuestionsByMode(QuestionMode.TOEIC);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 토익 파트별 문제 조회
    @GetMapping("/toeic/{part}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getToeicQuestionsByPart(
            @PathVariable ToeicPart part) {

        List<QuestionResponse> questions = questionService.getToeicQuestionsByPart(part);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 영작 문제 조회
    @GetMapping("/writing")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getWritingQuestions() {
        List<QuestionResponse> questions = questionService.getQuestionsByMode(QuestionMode.WRITING);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 주제별 문제 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> searchQuestions(
            @RequestParam String topic) {

        List<QuestionResponse> questions = questionService.searchQuestions(topic);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    // 문제 상세 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(
            @PathVariable Long questionId) {

        QuestionResponse response = questionService.getQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 문제 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long questionId) {

        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success("문제가 삭제되었습니다."));
    }

    // 문제 개수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getQuestionCount() {
        long count = questionService.getQuestionCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 토익 문제 개수 조회
    @GetMapping("/toeic/count")
    public ResponseEntity<ApiResponse<Long>> getToeicQuestionCount() {
        long count = questionService.getQuestionCountByMode(QuestionMode.TOEIC);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 영작 문제 개수 조회
    @GetMapping("/writing/count")
    public ResponseEntity<ApiResponse<Long>> getWritingQuestionCount() {
        long count = questionService.getQuestionCountByMode(QuestionMode.WRITING);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}