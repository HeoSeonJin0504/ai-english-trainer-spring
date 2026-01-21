package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;  // 로그인 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 비밀번호 변경 메서드
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 회원정보 수정 메서드
    public void updateInfo(String phone, String email, Gender gender, Integer age) {
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
        if (gender != null) this.gender = gender;
        if (age != null) this.age = age;
    }
}