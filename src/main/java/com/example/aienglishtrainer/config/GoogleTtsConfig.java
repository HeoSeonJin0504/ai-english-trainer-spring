package com.example.aienglishtrainer.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class GoogleTtsConfig {

    @Value("${google.cloud.credentials-path}")
    private String credentialsPath;

    @Bean
    public TextToSpeechClient textToSpeechClient() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new FileInputStream(credentialsPath));

            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();

            log.info("Google TTS 클라이언트 초기화 성공");
            return TextToSpeechClient.create(settings);

        } catch (IOException e) {
            log.warn("Google TTS 클라이언트 초기화 실패: {}", e.getMessage());
            log.warn("TTS 기능이 비활성화됩니다. Web Speech API를 사용해주세요.");
            return null;
        }
    }
}