package com.example.aienglishtrainer.config;

import com.example.aienglishtrainer.oauth.CustomOAuth2UserService;
import com.example.aienglishtrainer.oauth.OAuth2AuthenticationFailureHandler;
import com.example.aienglishtrainer.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.aienglishtrainer.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT + Cookie 방식)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함 (JWT Stateless)
                // 단, OAuth2 인증 흐름 중 state 파라미터 검증을 위해 세션이 일시적으로 필요하므로
                // STATELESS 대신 IF_REQUIRED 사용
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // 인증 없이 접근 가능한 경로
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/check-username",
                                "/api/auth/check-phone",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/login"              // ← 추가
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")          // ← 추가 (Spring 기본 UI 비활성화)
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )

                // JWT 필터: 기존 일반 로그인 인증에 사용 (OAuth2 흐름에는 관여하지 않음)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}