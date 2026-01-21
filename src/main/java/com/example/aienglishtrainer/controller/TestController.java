package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public String hello() {
        return "AI English Trainer API 서버가 정상 작동 중입니다!";
    }

    // 인증 필요한 테스트 API
    @GetMapping("/api/test/auth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> authTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", SecurityUtil.getCurrentUserId());
        data.put("username", SecurityUtil.getCurrentUsername());
        data.put("message", "인증된 사용자입니다!");

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}