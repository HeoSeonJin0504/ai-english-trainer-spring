package com.example.aienglishtrainer.dto.word;

import com.example.aienglishtrainer.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class WordResponse {

    private Long id;
    private String word;
    private String partOfSpeech;
    private String meaning;
    private LocalDateTime createdAt;

    public static WordResponse from(Word word) {
        return WordResponse.builder()
                .id(word.getId())
                .word(word.getWord())
                .partOfSpeech(word.getPartOfSpeech())
                .meaning(word.getMeaning())
                .createdAt(word.getCreatedAt())
                .build();
    }
}