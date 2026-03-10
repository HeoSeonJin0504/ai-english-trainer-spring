package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.word.ExampleRequest;
import com.example.aienglishtrainer.dto.word.ExampleResponse;
import com.example.aienglishtrainer.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    // 예문 저장
    @PostMapping
    public ResponseEntity<ApiResponse<ExampleResponse>> saveExample(@RequestBody ExampleRequest request) {
        ExampleResponse response = exampleService.saveExample(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 예문 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExampleResponse>>> getMyExamples() {
        List<ExampleResponse> examples = exampleService.getMyExamples();
        return ResponseEntity.ok(ApiResponse.success(examples));
    }

    // 예문 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ExampleResponse>>> searchExamples(@RequestParam String keyword) {
        List<ExampleResponse> examples = exampleService.searchExamples(keyword);
        return ResponseEntity.ok(ApiResponse.success(examples));
    }

    // 예문 개수
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getExampleCount() {
        long count = exampleService.getExampleCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 특정 단어의 예문 조회
    @GetMapping("/word/{wordId}")
    public ResponseEntity<ApiResponse<List<ExampleResponse>>> getExamplesByWord(@PathVariable Long wordId) {
        List<ExampleResponse> examples = exampleService.getExamplesByWord(wordId);
        return ResponseEntity.ok(ApiResponse.success(examples));
    }

    // 예문 상세 조회
    @GetMapping("/{exampleId}")
    public ResponseEntity<ApiResponse<ExampleResponse>> getExample(@PathVariable Long exampleId) {
        ExampleResponse example = exampleService.getExample(exampleId);
        return ResponseEntity.ok(ApiResponse.success(example));
    }

    // 예문 삭제
    @DeleteMapping("/{exampleId}")
    public ResponseEntity<ApiResponse<Void>> deleteExample(@PathVariable Long exampleId) {
        exampleService.deleteExample(exampleId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}