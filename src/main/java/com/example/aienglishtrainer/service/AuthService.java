package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.user.*;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.exception.DuplicateException;
import com.example.aienglishtrainer.exception.UnauthorizedException;
import com.example.aienglishtrainer.repository.UserRepository;
import com.example.aienglishtrainer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // 아이디 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateException("이미 사용 중인 아이디입니다.");
        }

        // 핸드폰 번호 중복 확인
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateException("이미 등록된 핸드폰 번호입니다.");
        }

        // 이메일 중복 확인 (입력된 경우만)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateException("이미 등록된 이메일입니다.");
            }
        }

        // 비밀번호 암호화 후 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .gender(request.getGender())
                .age(request.getAge())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    // 아이디 중복 확인
    public boolean checkUsername(String username) {
        return !userRepository.existsByUsername(username);
    }

    // 핸드폰 번호 중복 확인
    public boolean checkPhone(String phone) {
        return !userRepository.existsByPhone(phone);
    }
}