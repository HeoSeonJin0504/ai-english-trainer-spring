package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.word.WordRequest;
import com.example.aienglishtrainer.dto.word.WordResponse;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.entity.Word;
import com.example.aienglishtrainer.exception.BusinessException;
import com.example.aienglishtrainer.exception.DuplicateException;
import com.example.aienglishtrainer.repository.UserRepository;
import com.example.aienglishtrainer.repository.WordRepository;
import com.example.aienglishtrainer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordService {

    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    // 단어 저장
    @Transactional
    public WordResponse saveWord(WordRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 중복 확인 (같은 단어 + 같은 품사)
        if (wordRepository.existsByUserIdAndWordAndPartOfSpeech(
                userId, request.getWord(), request.getPartOfSpeech())) {
            throw new DuplicateException("이미 저장된 단어입니다: " + request.getWord() + " (" + request.getPartOfSpeech() + ")");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Word word = Word.builder()
                .user(user)
                .word(request.getWord())
                .partOfSpeech(request.getPartOfSpeech())
                .meaning(request.getMeaning())
                .build();

        Word saved = wordRepository.save(word);
        return WordResponse.from(saved);
    }

    // 내 단어 전체 조회
    public List<WordResponse> getMyWords() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Word> words = wordRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return words.stream()
                .map(WordResponse::from)
                .collect(Collectors.toList());
    }

    // 단어 검색
    public List<WordResponse> searchWords(String keyword) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Word> words = wordRepository.findByUserIdAndWordContainingIgnoreCaseOrderByCreatedAtDesc(userId, keyword);

        return words.stream()
                .map(WordResponse::from)
                .collect(Collectors.toList());
    }

    // 단어 상세 조회
    public WordResponse getWord(Long wordId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Word word = wordRepository.findByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new BusinessException("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return WordResponse.from(word);
    }

    // 단어 삭제
    @Transactional
    public void deleteWord(Long wordId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Word word = wordRepository.findByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new BusinessException("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        wordRepository.delete(word);
    }

    // 단어 개수
    public long getWordCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return wordRepository.countByUserId(userId);
    }
}