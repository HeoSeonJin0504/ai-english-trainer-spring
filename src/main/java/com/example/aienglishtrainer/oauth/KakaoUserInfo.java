package com.example.aienglishtrainer.oauth;

import java.util.Map;

/**
 * Kakao OAuth2 응답 attributes에서 사용자 정보를 추출한다.
 *
 * Kakao attributes 예시:
 * {
 *   "id": 1234567890,                            // providerId
 *   "kakao_account": {
 *     "email": "user@kakao.com",                 // 이메일 (선택 동의)
 *     "has_email": true,
 *     "is_email_verified": true
 *   }
 * }
 */
public class KakaoUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        // kakao_account 하위에 이메일이 있음
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }
}