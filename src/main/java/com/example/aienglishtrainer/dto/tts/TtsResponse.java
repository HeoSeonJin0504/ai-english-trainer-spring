package com.example.aienglishtrainer.dto.tts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TtsResponse {

    private String audio;  // Base64 인코딩된 오디오
    private String contentType;
    private int textLength;
}