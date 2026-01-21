package com.example.aienglishtrainer.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateExampleResponse {

    @JsonProperty("isValid")
    private boolean isValid;
    private String errorMessage;
    private WordInfo word;
    private List<Example> examples;
    private RelatedWords relatedWords;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WordInfo {
        private String original;
        private List<Meaning> meanings;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meaning {
        private String partOfSpeech;
        private String meaning;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Example {
        private String english;
        private String korean;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RelatedWords {
        private RelatedWord synonym;
        private RelatedWord antonym;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RelatedWord {
        private String word;
        private String partOfSpeech;
        private String meaning;
    }
}