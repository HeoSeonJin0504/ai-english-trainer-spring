package com.example.aienglishtrainer.oauth;

import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth 로그인 성공 시 호출되는 핸들러.
 * Node.js의 oauthCallback()과 동일한 역할:
 *   1. CustomOAuth2User에서 User 꺼내기
 *   2. JWT 발급
 *   3. httpOnly Cookie에 세팅
 *   4. 프론트엔드로 리다이렉트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oauth.client-redirect-url}")
    private String clientRedirectUrl;  // 예: http://localhost:5173/oauth/success

    @Value("${jwt.expiration}")
    private long jwtExpiration;  // 밀리초

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // JWT 발급
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());

        // httpOnly Cookie 세팅 (Node.js의 setTokenCookie()와 동일)
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));  // 밀리초 → 초 변환

        // 운영 환경에서는 Secure 플래그 활성화 (HTTPS 필수)
        // cookie.setSecure(true);

        response.addCookie(cookie);

        log.info("OAuth 로그인 성공 - userId: {}, username: {}, redirect: {}",
                user.getId(), user.getUsername(), clientRedirectUrl);

        // 프론트엔드로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, clientRedirectUrl);
    }
}