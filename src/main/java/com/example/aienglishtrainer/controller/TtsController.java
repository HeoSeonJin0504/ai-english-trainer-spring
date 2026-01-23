package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.tts.TtsRequest;
import com.example.aienglishtrainer.dto.tts.TtsResponse;
import com.example.aienglishtrainer.dto.tts.TtsStatusResponse;
import com.example.aienglishtrainer.service.TtsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

    // 텍스트를 음성으로 변환
    @PostMapping("/speak")
    public ResponseEntity<ApiResponse<TtsResponse>> speak(
            @Valid @RequestBody TtsRequest request) {

        TtsResponse response = ttsService.synthesizeSpeech(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // TTS 서비스 상태 확인
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<TtsStatusResponse>> status() {
        TtsStatusResponse response = ttsService.checkStatus();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}