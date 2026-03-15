package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.example.aienglishtrainer.entity.Gender;

import java.time.LocalDateTime;

/**
 * [기존 필드에서 추가된 부분]
 * - provider  : 로그인 방식 ("LOCAL", "GOOGLE", "KAKAO", "NAVER")
 * - providerId: OAuth Provider가 발급한 고유 ID (LOCAL이면 null)
 *
 * OAuth 사용자는 password, phone, gender, age가 null일 수 있으므로
 * 기존 allowNull=false였던 필드들을 nullable로 변경해야 함.
 * (아래 주석으로 표시)
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // 같은 Provider에서 동일 providerId 중복 가입 방지 (Node.js unique_provider_providerId와 동일)
                @UniqueConstraint(name = "unique_provider_provider_id", columnNames = {"provider", "provider_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // OAuth 사용자는 비밀번호 없음 → nullable
    @Column
    private String password;

    // OAuth 사용자는 전화번호 없음 → nullable
    @Column(unique = true, length = 20)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 10)
    private Gender gender;

    private Integer age;

    /**
     * 로그인 방식: "LOCAL", "GOOGLE", "KAKAO", "NAVER"
     * 기본값 "LOCAL" (기존 일반 로그인 사용자)
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String provider = "LOCAL";

    /**
     * OAuth Provider가 발급한 사용자 고유 ID.
     * LOCAL 사용자는 null.
     */
    @Column(name = "provider_id", length = 100)
    private String providerId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}