package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.user.*;
import com.example.aienglishtrainer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {

        UserResponse response = authService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", response));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    // 아이디 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(
            @RequestParam String username) {

        boolean available = authService.checkUsername(username);
        String message = available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        return ResponseEntity.ok(ApiResponse.success(message, available));
    }

    // 핸드폰 번호 중복 확인
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(
            @RequestParam String phone) {

        boolean available = authService.checkPhone(phone);
        String message = available ? "사용 가능한 번호입니다." : "이미 등록된 번호입니다.";
        return ResponseEntity.ok(ApiResponse.success(message, available));
    }
}