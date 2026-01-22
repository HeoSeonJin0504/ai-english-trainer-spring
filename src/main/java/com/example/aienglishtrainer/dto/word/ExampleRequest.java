package com.example.aienglishtrainer.dto.word;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExampleRequest {

    @NotBlank(message = "영어 예문은 필수입니다.")
    private String english;

    @NotBlank(message = "한국어 번역은 필수입니다.")
    private String korean;

    private Long wordId;  // 연결할 단어 ID (선택)
}