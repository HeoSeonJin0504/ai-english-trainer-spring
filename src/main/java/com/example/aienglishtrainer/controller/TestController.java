package com.example.aienglishtrainer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String hello() {
        return "AI English Trainer API 서버가 정상 작동 중입니다!";
    }
}