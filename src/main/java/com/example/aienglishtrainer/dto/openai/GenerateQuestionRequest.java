package com.example.aienglishtrainer.dto.openai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateQuestionRequest {

    @NotBlank(message = "주제는 필수입니다.")
    private String topic;

    @NotBlank(message = "모드는 필수입니다.")
    @Pattern(regexp = "^(toeic|writing)$", message = "모드는 toeic 또는 writing만 가능합니다.")
    private String mode;
}