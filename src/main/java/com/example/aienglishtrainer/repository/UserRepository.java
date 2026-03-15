package com.example.aienglishtrainer.repository;

import com.example.aienglishtrainer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디로 회원 조회
    Optional<User> findByUsername(String username);

    // 핸드폰 번호로 회원 조회
    Optional<User> findByPhone(String phone);

    // 이메일로 회원 조회
    Optional<User> findByEmail(String email);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 핸드폰 번호 중복 확인
    boolean existsByPhone(String phone);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // OAuth
    /**
     * provider + providerId로 기존 OAuth 회원 조회.
     * CustomOAuth2UserService에서 기존 회원 여부를 확인할 때 사용.
     * (Node.js의 User.findOne({ where: { provider, providerId } })와 동일)
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}