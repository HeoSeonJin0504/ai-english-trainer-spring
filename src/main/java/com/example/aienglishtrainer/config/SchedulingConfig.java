package com.example.aienglishtrainer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄링 활성화
 * - ChatbotService의 @Scheduled 메서드 실행을 위해 필요
 * - 10분마다 오래된 대화 세션 자동 정리
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}