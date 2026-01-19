package com.example.aienglishtrainer.dto.user;

import com.example.aienglishtrainer.entity.Gender;
import com.example.aienglishtrainer.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private Gender gender;
    private Integer age;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .gender(user.getGender())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }
}