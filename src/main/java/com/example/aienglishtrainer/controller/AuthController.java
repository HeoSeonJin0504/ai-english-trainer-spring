package com.example.aienglishtrainer.controller;

import com.example.aienglishtrainer.dto.ApiResponse;
import com.example.aienglishtrainer.dto.user.*;
import com.example.aienglishtrainer.security.JwtTokenProvider;
import com.example.aienglishtrainer.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.exception.BusinessException;
import com.example.aienglishtrainer.repository.UserRepository;
import com.example.aienglishtrainer.security.SecurityUtil;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse userResponse = authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", userResponse));
    }

    // 로그인 - JWT를 httpOnly Cookie로 발급
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        // 로그인 처리 (토큰 생성 포함)
        LoginResponse loginResponse = authService.login(request);

        // httpOnly Cookie에 JWT 저장
        Cookie jwtCookie = new Cookie("accessToken", loginResponse.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);       // HTTPS 환경에서는 true로 변경
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) (jwtTokenProvider.getExpirationTime() / 1000));
        response.addCookie(jwtCookie);

        // accessToken은 Cookie로 전달하므로 응답 바디에서 제외
        LoginResponse safeResponse = LoginResponse.builder()
                .user(loginResponse.getUser())
                .build();

        return ResponseEntity.ok(ApiResponse.success(safeResponse));
    }

    // 로그아웃 - Cookie 무효화
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);           // 즉시 만료
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 아이디 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean available = authService.checkUsername(username);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    // 핸드폰 번호 중복 확인
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(@RequestParam String phone) {
        boolean available = authService.checkPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
}