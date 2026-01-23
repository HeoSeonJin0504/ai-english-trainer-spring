package com.example.aienglishtrainer.dto.question;

import com.example.aienglishtrainer.entity.Question;
import com.example.aienglishtrainer.entity.QuestionMode;
import com.example.aienglishtrainer.entity.ToeicPart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private QuestionMode mode;
    private ToeicPart toeicPart;
    private String writingType;
    private String topic;
    private String passage;
    private String insertSentence;
    private String question;
    private Map<String, String> options;
    private String answer;
    private String hint;
    private String explanation;
    private LocalDateTime createdAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static QuestionResponse from(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .mode(question.getMode())
                .toeicPart(question.getToeicPart())
                .writingType(question.getWritingType())
                .topic(question.getTopic())
                .passage(question.getPassage())
                .insertSentence(question.getInsertSentence())
                .question(question.getQuestion())
                .options(parseOptions(question.getOptions()))
                .answer(question.getAnswer())
                .hint(question.getHint())
                .explanation(question.getExplanation())
                .createdAt(question.getCreatedAt())
                .build();
    }

    private static Map<String, String> parseOptions(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}