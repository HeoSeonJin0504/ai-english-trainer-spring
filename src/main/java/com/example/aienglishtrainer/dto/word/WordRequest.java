package com.example.aienglishtrainer.dto.word;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WordRequest {

    @NotBlank(message = "단어는 필수입니다.")
    private String word;

    @NotBlank(message = "품사는 필수입니다.")
    private String partOfSpeech;

    @NotBlank(message = "뜻은 필수입니다.")
    private String meaning;
}