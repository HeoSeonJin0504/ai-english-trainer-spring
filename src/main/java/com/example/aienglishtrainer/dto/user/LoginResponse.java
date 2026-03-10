package com.example.aienglishtrainer.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    // httpOnly Cookie로 전달하므로 응답 바디에서는 선택적으로 포함
    private String accessToken;
    private String tokenType;
    private Long expiresIn;

    // 프론트엔드가 localStorage에 저장하는 사용자 정보
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String phone;
        private String email;
    }
}