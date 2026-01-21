package com.example.aienglishtrainer.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateQuestionResponse {

    private String mode;
    private ToeicQuestions questions;  // toeic 모드용
    private List<WritingQuestion> writingQuestions;  // writing 모드용

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToeicQuestions {
        private List<Part5Question> part5;
        private List<Part6Question> part6;
        private List<Part7Question> part7;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part5Question {
        private String question;
        private Map<String, String> options;
        private String answer;
        private String explanation;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part6Question {
        private String passage;
        private String insertSentence;
        private String question;
        private Map<String, String> options;
        private String answer;
        private String explanation;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part7Question {
        private String passage;
        private String question;
        private Map<String, String> options;
        private String answer;
        private String explanation;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WritingQuestion {
        private String type;
        private String question;
        private String hint;
        private String answer;
    }
}