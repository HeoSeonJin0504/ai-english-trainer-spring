package com.example.aienglishtrainer.oauth;

import java.util.Map;

/**
 * Naver OAuth2 응답 attributes에서 사용자 정보를 추출한다.
 *
 * Naver attributes 예시:
 * {
 *   "response": {
 *     "id": "abcdef1234",          // providerId
 *     "email": "user@naver.com"
 *   }
 * }
 * 네이버는 response 키 하위에 실제 정보가 있음에 주의.
 */
public class NaverUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "NAVER";
    }

    @Override
    public String getProviderId() {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) return null;
        return (String) response.get("id");
    }

    @Override
    public String getEmail() {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) return null;
        return (String) response.get("email");
    }
}