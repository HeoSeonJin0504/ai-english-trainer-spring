package com.example.aienglishtrainer.dto.tts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TtsStatusResponse {

    private boolean available;
    private String message;
}