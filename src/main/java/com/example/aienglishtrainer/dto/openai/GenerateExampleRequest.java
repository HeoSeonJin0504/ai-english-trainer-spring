package com.example.aienglishtrainer.dto.openai;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateExampleRequest {

    @NotBlank(message = "단어는 필수입니다.")
    private String word;
}