package com.example.aienglishtrainer.oauth;

import java.util.Map;

/**
 * Spring Security가 넘겨주는 registrationId("google", "kakao", "naver")를 보고
 * 적절한 OAuthUserInfo 구현체를 생성해서 반환하는 팩토리 클래스.
 */
public class OAuthUserInfoFactory {

    public static OAuthUserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao"  -> new KakaoUserInfo(attributes);
            case "naver"  -> new NaverUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth Provider: " + registrationId);
        };
    }
}