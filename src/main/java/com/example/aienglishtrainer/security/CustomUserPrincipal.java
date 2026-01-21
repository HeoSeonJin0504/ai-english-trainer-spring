package com.example.aienglishtrainer.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {

    private Long userId;
    private String username;
}