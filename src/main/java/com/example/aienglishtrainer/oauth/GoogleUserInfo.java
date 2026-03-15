package com.example.aienglishtrainer.oauth;

import java.util.Map;

/**
 * Google OAuth2 응답 attributes에서 사용자 정보를 추출한다.
 *
 * Google attributes 예시:
 * {
 *   "sub": "1234567890",           // providerId
 *   "email": "user@gmail.com",
 *   "name": "홍길동"
 * }
 */
public class GoogleUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "GOOGLE";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}