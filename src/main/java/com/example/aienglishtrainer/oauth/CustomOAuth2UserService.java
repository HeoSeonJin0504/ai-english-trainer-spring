package com.example.aienglishtrainer.oauth;

import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth 로그인 성공 시 Spring Security가 호출하는 서비스.
 *
 * 처리 흐름:
 * 1. Provider로부터 사용자 정보(attributes) 수신
 * 2. provider + providerId로 기존 회원 조회
 * 3. 신규 회원이면 자동 가입 (username 중복 시 suffix 추가)
 * 4. CustomOAuth2User 반환 → SecurityContext에 저장됨
 *    → OAuth2AuthenticationSuccessHandler에서 JWT 발급에 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 구현체로 Provider에서 사용자 attributes를 가져옴
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2. Provider 식별 (google / kakao / naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. Provider별 사용자 정보 추출
        OAuthUserInfo userInfo = OAuthUserInfoFactory.of(registrationId, oAuth2User.getAttributes());

        // 4. DB에서 기존 회원 조회 또는 신규 가입
        User user = getOrCreateUser(userInfo);

        log.info("OAuth 로그인 성공 - provider: {}, userId: {}, username: {}",
                userInfo.getProvider(), user.getId(), user.getUsername());

        // 5. SecurityContext에 올라갈 CustomOAuth2User 반환 (userId, username 포함)
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    /**
     * 기존 회원이면 반환, 신규면 생성.
     * Node.js의 passport verify callback과 동일한 역할.
     */
    private User getOrCreateUser(OAuthUserInfo userInfo) {
        return userRepository
                .findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> createNewUser(userInfo));
    }

    private User createNewUser(OAuthUserInfo userInfo) {
        String username = generateUniqueUsername(userInfo.getEmail(), userInfo.getProvider(), userInfo.getProviderId());

        User newUser = User.builder()
                .username(username)
                .email(userInfo.getEmail())
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Node.js의 generateUniqueUsername()과 동일한 로직.
     * 1순위: 이메일 앞부분 (user@gmail.com → user)
     * 2순위: provider_id 뒤 6자리
     * 중복 시: username_1, username_2, ... 순으로 suffix 추가
     */
    private String generateUniqueUsername(String email, String provider, String providerId) {
        String base;
        if (email != null && !email.isBlank()) {
            // 이메일 앞부분에서 특수문자를 _ 로 치환
            base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_");
        } else {
            // providerId 마지막 6자리 사용
            String shortId = providerId.length() > 6
                    ? providerId.substring(providerId.length() - 6)
                    : providerId;
            base = provider.toLowerCase() + "_" + shortId;
        }

        // 최대 20자 제한
        if (base.length() > 20) {
            base = base.substring(0, 20);
        }

        // 중복 확인 및 suffix 추가
        String candidate = base;
        int count = 1;
        while (userRepository.existsByUsername(candidate)) {
            String suffix = "_" + count;
            candidate = base.substring(0, Math.min(base.length(), 20 - suffix.length())) + suffix;
            count++;
        }

        return candidate;
    }
}