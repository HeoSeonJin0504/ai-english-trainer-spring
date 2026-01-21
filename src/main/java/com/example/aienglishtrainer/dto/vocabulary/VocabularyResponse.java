package com.example.aienglishtrainer.dto.vocabulary;

import com.example.aienglishtrainer.entity.Vocabulary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class VocabularyResponse {

    private Long id;
    private String word;
    private List<MeaningDto> meanings;
    private List<ExampleDto> examples;
    private RelatedWordDto synonym;
    private RelatedWordDto antonym;
    private LocalDateTime createdAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MeaningDto {
        private String partOfSpeech;
        private String meaning;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ExampleDto {
        private String english;
        private String korean;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class RelatedWordDto {
        private String word;
        private String partOfSpeech;
        private String meaning;
    }

    // Entity -> DTO 변환
    public static VocabularyResponse from(Vocabulary vocabulary) {
        return VocabularyResponse.builder()
                .id(vocabulary.getId())
                .word(vocabulary.getWord())
                .meanings(parseList(vocabulary.getMeanings(), new TypeReference<>() {}))
                .examples(parseList(vocabulary.getExamples(), new TypeReference<>() {}))
                .synonym(parseObject(vocabulary.getSynonym(), RelatedWordDto.class))
                .antonym(parseObject(vocabulary.getAntonym(), RelatedWordDto.class))
                .createdAt(vocabulary.getCreatedAt())
                .build();
    }

    private static <T> List<T> parseList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}