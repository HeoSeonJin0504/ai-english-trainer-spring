package com.example.aienglishtrainer.dto.word;

import com.example.aienglishtrainer.entity.Example;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ExampleResponse {

    private Long id;
    private String english;
    private String korean;
    private WordInfo word;  // 연결된 단어 정보
    private LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class WordInfo {
        private Long id;
        private String word;
        private String partOfSpeech;
        private String meaning;
    }

    public static ExampleResponse from(Example example) {
        WordInfo wordInfo = null;
        if (example.getWord() != null) {
            wordInfo = WordInfo.builder()
                    .id(example.getWord().getId())
                    .word(example.getWord().getWord())
                    .partOfSpeech(example.getWord().getPartOfSpeech())
                    .meaning(example.getWord().getMeaning())
                    .build();
        }

        return ExampleResponse.builder()
                .id(example.getId())
                .english(example.getEnglish())
                .korean(example.getKorean())
                .word(wordInfo)
                .createdAt(example.getCreatedAt())
                .build();
    }
}