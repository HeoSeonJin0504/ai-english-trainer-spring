package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.word.WordRequest;
import com.example.aienglishtrainer.dto.word.WordResponse;
import com.example.aienglishtrainer.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    // 단어 저장
    @PostMapping
    public ResponseEntity<ApiResponse<WordResponse>> saveWord(@RequestBody WordRequest request) {
        WordResponse response = wordService.saveWord(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 단어 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<WordResponse>>> getMyWords() {
        List<WordResponse> words = wordService.getMyWords();
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    // 단어 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<WordResponse>>> searchWords(@RequestParam String keyword) {
        List<WordResponse> words = wordService.searchWords(keyword);
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    // 단어 개수
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getWordCount() {
        long count = wordService.getWordCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 단어 상세 조회
    @GetMapping("/{wordId}")
    public ResponseEntity<ApiResponse<WordResponse>> getWord(@PathVariable Long wordId) {
        WordResponse word = wordService.getWord(wordId);
        return ResponseEntity.ok(ApiResponse.success(word));
    }

    // 단어 삭제
    @DeleteMapping("/{wordId}")
    public ResponseEntity<ApiResponse<Void>> deleteWord(@PathVariable Long wordId) {
        wordService.deleteWord(wordId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("삭제되었습니다.")
                .build());
    }
}