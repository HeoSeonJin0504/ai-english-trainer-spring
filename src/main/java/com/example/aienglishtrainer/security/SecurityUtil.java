package com.example.aienglishtrainer.security;

import com.example.aienglishtrainer.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // 현재 로그인한 사용자 ID 반환
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getUserId();
        }

        throw new UnauthorizedException("유효하지 않은 인증 정보입니다.");
    }

    // 현재 로그인한 사용자 username 반환
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getUsername();
        }

        throw new UnauthorizedException("유효하지 않은 인증 정보입니다.");
    }
}