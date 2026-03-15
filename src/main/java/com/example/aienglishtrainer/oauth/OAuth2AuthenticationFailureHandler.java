package com.example.aienglishtrainer.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth 로그인 실패 시 호출되는 핸들러.
 * Node.js의 oauthFailure()에 해당하나,
 * 리다이렉트 방식이므로 프론트로 실패 URL을 전달한다.
 */
@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${oauth.client-failure-url}")
    private String clientFailureUrl;  // 예: http://localhost:5173/oauth/failure

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.warn("OAuth 로그인 실패: {}", exception.getMessage());
        getRedirectStrategy().sendRedirect(request, response, clientFailureUrl);
    }
}