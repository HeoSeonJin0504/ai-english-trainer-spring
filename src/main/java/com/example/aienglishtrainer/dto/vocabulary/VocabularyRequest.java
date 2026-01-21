package com.example.aienglishtrainer.dto.vocabulary;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class VocabularyRequest {

    @NotBlank(message = "단어는 필수입니다.")
    private String word;

    private List<MeaningDto> meanings;
    private List<ExampleDto> examples;
    private RelatedWordDto synonym;
    private RelatedWordDto antonym;

    @Getter
    @NoArgsConstructor
    public static class MeaningDto {
        private String partOfSpeech;
        private String meaning;
    }

    @Getter
    @NoArgsConstructor
    public static class ExampleDto {
        private String english;
        private String korean;
    }

    @Getter
    @NoArgsConstructor
    public static class RelatedWordDto {
        private String word;
        private String partOfSpeech;
        private String meaning;
    }
}