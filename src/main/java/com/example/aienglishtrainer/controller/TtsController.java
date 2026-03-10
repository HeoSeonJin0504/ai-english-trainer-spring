package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.tts.TtsRequest;
import com.example.aienglishtrainer.dto.tts.TtsResponse;
import com.example.aienglishtrainer.dto.tts.TtsStatusResponse;
import com.example.aienglishtrainer.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

    // TTS 음성 생성
    @PostMapping("/speak")
    public ResponseEntity<ApiResponse<TtsResponse>> synthesizeSpeech(
            @RequestBody TtsRequest request) {

        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("텍스트를 입력해주세요."));
        }

        TtsResponse response = ttsService.synthesizeSpeech(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // TTS 서비스 상태 확인
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<TtsStatusResponse>> checkStatus() {
        TtsStatusResponse status = ttsService.checkStatus();
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}