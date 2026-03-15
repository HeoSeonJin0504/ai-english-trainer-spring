package com.example.aienglishtrainer.oauth;

import com.example.aienglishtrainer.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * SecurityContext에 저장될 OAuth2User 구현체.
 * SuccessHandler에서 user.getId(), user.getUsername()으로 JWT를 만들기 위해
 * User 엔티티를 함께 보관한다.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // OAuth2User의 getName()은 식별자를 반환 (Spring Security 내부에서 사용)
    @Override
    public String getName() {
        return user.getUsername();
    }
}