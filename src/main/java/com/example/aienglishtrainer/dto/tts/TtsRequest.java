package com.example.aienglishtrainer.dto.tts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TtsRequest {

    @NotBlank(message = "텍스트는 필수입니다.")
    @Size(max = 1000, message = "텍스트는 1000자 이하여야 합니다.")
    private String text;

    private Double speed = 1.0;  // 기본값 1.0

    private String voice = "female";  // female 또는 male
}