package com.example.aienglishtrainer.service;

import com.example.aienglishtrainer.dto.word.ExampleRequest;
import com.example.aienglishtrainer.dto.word.ExampleResponse;
import com.example.aienglishtrainer.entity.Example;
import com.example.aienglishtrainer.entity.User;
import com.example.aienglishtrainer.entity.Word;
import com.example.aienglishtrainer.exception.BusinessException;
import com.example.aienglishtrainer.exception.DuplicateException;
import com.example.aienglishtrainer.repository.ExampleRepository;
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
public class ExampleService {

    private final ExampleRepository exampleRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    // 예문 저장
    @Transactional
    public ExampleResponse saveExample(ExampleRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 중복 확인
        if (exampleRepository.existsByUserIdAndEnglish(userId, request.getEnglish())) {
            throw new DuplicateException("이미 저장된 예문입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 연결할 단어 조회 (선택)
        Word word = null;
        if (request.getWordId() != null) {
            word = wordRepository.findByIdAndUserId(request.getWordId(), userId)
                    .orElseThrow(() -> new BusinessException("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        }

        Example example = Example.builder()
                .user(user)
                .word(word)
                .english(request.getEnglish())
                .korean(request.getKorean())
                .build();

        Example saved = exampleRepository.save(example);
        return ExampleResponse.from(saved);
    }

    // 내 예문 전체 조회
    public List<ExampleResponse> getMyExamples() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Example> examples = exampleRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return examples.stream()
                .map(ExampleResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 단어의 예문 조회
    public List<ExampleResponse> getExamplesByWord(Long wordId) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Example> examples = exampleRepository.findByUserIdAndWordIdOrderByCreatedAtDesc(userId, wordId);

        return examples.stream()
                .map(ExampleResponse::from)
                .collect(Collectors.toList());
    }

    // 예문 검색
    public List<ExampleResponse> searchExamples(String keyword) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Example> examples = exampleRepository.findByUserIdAndEnglishContainingIgnoreCaseOrderByCreatedAtDesc(userId, keyword);

        return examples.stream()
                .map(ExampleResponse::from)
                .collect(Collectors.toList());
    }

    // 예문 상세 조회
    public ExampleResponse getExample(Long exampleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Example example = exampleRepository.findByIdAndUserId(exampleId, userId)
                .orElseThrow(() -> new BusinessException("예문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return ExampleResponse.from(example);
    }

    // 예문 삭제
    @Transactional
    public void deleteExample(Long exampleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Example example = exampleRepository.findByIdAndUserId(exampleId, userId)
                .orElseThrow(() -> new BusinessException("예문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        exampleRepository.delete(example);
    }

    // 예문 개수
    public long getExampleCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return exampleRepository.countByUserId(userId);
    }
}