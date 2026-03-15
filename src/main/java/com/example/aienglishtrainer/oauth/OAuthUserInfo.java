package com.example.aienglishtrainer.oauth;

/**
 * 각 OAuth Provider(Google, Kakao, Naver)에서 공통으로 사용할 사용자 정보 추출 인터페이스.
 * Provider마다 응답 구조가 달라서 각각 구현체를 만든다.
 */
public interface OAuthUserInfo {

    // OAuth Provider 종류 ("GOOGLE", "KAKAO", "NAVER")
    String getProvider();

    // Provider에서 발급한 고유 식별자
    String getProviderId();

    // 이메일 (없을 수 있음)
    String getEmail();
}