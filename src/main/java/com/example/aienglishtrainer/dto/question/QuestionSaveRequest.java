package com.example.aienglishtrainer.dto.question;

import com.example.aienglishtrainer.entity.QuestionMode;
import com.example.aienglishtrainer.entity.ToeicPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class QuestionSaveRequest {

    @NotNull(message = "문제 모드는 필수입니다.")
    private QuestionMode mode;

    private ToeicPart toeicPart;  // 토익용

    private String writingType;  // 영작용

    @NotBlank(message = "주제는 필수입니다.")
    private String topic;

    private String passage;  // Part6, Part7용

    private String insertSentence;  // Part6용

    @NotBlank(message = "문제 내용은 필수입니다.")
    private String question;

    private Map<String, String> options;  // 선택지

    @NotBlank(message = "정답은 필수입니다.")
    private String answer;

    private String hint;  // 영작용

    private String explanation;
}