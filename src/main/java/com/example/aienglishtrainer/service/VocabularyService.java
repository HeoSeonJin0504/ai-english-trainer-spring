package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.vocabulary.VocabularyRequest;
import com.example.aienglishtrainer.dto.vocabulary.VocabularyResponse;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.entity.Vocabulary;
import com.example.aienglishtrainer.exception.BusinessException;
import com.example.aienglishtrainer.exception.DuplicateException;
import com.example.aienglishtrainer.repository.UserRepository;
import com.example.aienglishtrainer.repository.VocabularyRepository;
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
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 단어 저장
    @Transactional
    public VocabularyResponse saveWord(VocabularyRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 중복 확인
        if (vocabularyRepository.existsByUserIdAndWord(userId, request.getWord())) {
            throw new DuplicateException("이미 저장된 단어입니다: " + request.getWord());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Vocabulary vocabulary = Vocabulary.builder()
                .user(user)
                .word(request.getWord())
                .meanings(toJson(request.getMeanings()))
                .examples(toJson(request.getExamples()))
                .synonym(toJson(request.getSynonym()))
                .antonym(toJson(request.getAntonym()))
                .build();

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return VocabularyResponse.from(saved);
    }

    // 내 단어 전체 조회
    public List<VocabularyResponse> getMyWords() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Vocabulary> words = vocabularyRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return words.stream()
                .map(VocabularyResponse::from)
                .collect(Collectors.toList());
    }

    // 단어 검색
    public List<VocabularyResponse> searchWords(String keyword) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Vocabulary> words = vocabularyRepository
                .findByUserIdAndWordContainingIgnoreCaseOrderByCreatedAtDesc(userId, keyword);

        return words.stream()
                .map(VocabularyResponse::from)
                .collect(Collectors.toList());
    }

    // 단어 상세 조회
    public VocabularyResponse getWord(Long wordId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Vocabulary vocabulary = vocabularyRepository.findByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new BusinessException("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return VocabularyResponse.from(vocabulary);
    }

    // 단어 삭제
    @Transactional
    public void deleteWord(Long wordId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Vocabulary vocabulary = vocabularyRepository.findByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new BusinessException("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        vocabularyRepository.delete(vocabulary);
    }

    // 내 단어 개수 조회
    public long getWordCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return vocabularyRepository.countByUserId(userId);
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