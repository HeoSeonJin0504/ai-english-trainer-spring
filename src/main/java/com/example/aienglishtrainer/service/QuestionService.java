package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.question.QuestionSaveRequest;
import com.example.aienglishtrainer.dto.question.QuestionResponse;
import com.example.aienglishtrainer.entity.Question;
import com.example.aienglishtrainer.entity.QuestionMode;
import com.example.aienglishtrainer.entity.ToeicPart;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.exception.BusinessException;
import com.example.aienglishtrainer.repository.QuestionRepository;
import com.example.aienglishtrainer.repository.UserRepository;
import com.example.aienglishtrainer.security.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 문제 저장
    @Transactional
    public QuestionResponse saveQuestion(QuestionSaveRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Question question = Question.builder()
                .user(user)
                .mode(request.getMode())
                .toeicPart(request.getToeicPart())
                .writingType(request.getWritingType())
                .topic(request.getTopic())
                .passage(request.getPassage())
                .insertSentence(request.getInsertSentence())
                .question(request.getQuestion())
                .options(toJson(request.getOptions()))
                .answer(request.getAnswer())
                .hint(request.getHint())
                .explanation(request.getExplanation())
                .build();

        Question saved = questionRepository.save(question);
        return QuestionResponse.from(saved);
    }

    // 내 문제 전체 조회
    public List<QuestionResponse> getMyQuestions() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Question> questions = questionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    // 모드별 문제 조회
    public List<QuestionResponse> getQuestionsByMode(QuestionMode mode) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Question> questions = questionRepository.findByUserIdAndModeOrderByCreatedAtDesc(userId, mode);

        return questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    // 토익 파트별 문제 조회
    public List<QuestionResponse> getToeicQuestionsByPart(ToeicPart part) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Question> questions = questionRepository.findByUserIdAndModeAndToeicPartOrderByCreatedAtDesc(
                userId, QuestionMode.TOEIC, part);

        return questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    // 주제별 문제 검색
    public List<QuestionResponse> searchQuestions(String topic) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Question> questions = questionRepository.findByUserIdAndTopicContainingIgnoreCaseOrderByCreatedAtDesc(userId, topic);

        return questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    // 문제 상세 조회
    public QuestionResponse getQuestion(Long questionId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Question question = questionRepository.findByIdAndUserId(questionId, userId)
                .orElseThrow(() -> new BusinessException("문제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return QuestionResponse.from(question);
    }

    // 문제 삭제
    @Transactional
    public void deleteQuestion(Long questionId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Question question = questionRepository.findByIdAndUserId(questionId, userId)
                .orElseThrow(() -> new BusinessException("문제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        questionRepository.delete(question);
    }

    // 문제 개수 조회
    public long getQuestionCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return questionRepository.countByUserId(userId);
    }

    // 모드별 문제 개수 조회
    public long getQuestionCountByMode(QuestionMode mode) {
        Long userId = SecurityUtil.getCurrentUserId();
        return questionRepository.countByUserIdAndMode(userId, mode);
    }

    // Object -> JSON 문자열 변환
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}