package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.vocabulary.VocabularyRequest;
import com.example.aienglishtrainer.dto.vocabulary.VocabularyResponse;
import com.example.aienglishtrainer.service.VocabularyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    // 단어 저장
    @PostMapping
    public ResponseEntity<ApiResponse<VocabularyResponse>> saveWord(
            @Valid @RequestBody VocabularyRequest request) {

        VocabularyResponse response = vocabularyService.saveWord(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("단어가 저장되었습니다.", response));
    }

    // 내 단어 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<VocabularyResponse>>> getMyWords() {
        List<VocabularyResponse> words = vocabularyService.getMyWords();
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    // 단어 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VocabularyResponse>>> searchWords(
            @RequestParam String keyword) {

        List<VocabularyResponse> words = vocabularyService.searchWords(keyword);
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    // 단어 상세 조회
    @GetMapping("/{wordId}")
    public ResponseEntity<ApiResponse<VocabularyResponse>> getWord(
            @PathVariable Long wordId) {

        VocabularyResponse response = vocabularyService.getWord(wordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 단어 삭제
    @DeleteMapping("/{wordId}")
    public ResponseEntity<ApiResponse<Void>> deleteWord(
            @PathVariable Long wordId) {

        vocabularyService.deleteWord(wordId);
        return ResponseEntity.ok(ApiResponse.success("단어가 삭제되었습니다."));
    }

    // 내 단어 개수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getWordCount() {
        long count = vocabularyService.getWordCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}