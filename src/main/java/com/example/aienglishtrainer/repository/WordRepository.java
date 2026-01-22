package com.example.aienglishtrainer.repository;

import com.example.aienglishtrainer.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // 사용자의 모든 단어 조회 (최신순)
    List<Word> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자의 특정 단어 조회
    Optional<Word> findByIdAndUserId(Long id, Long userId);

    // 중복 확인 (같은 단어 + 같은 품사)
    boolean existsByUserIdAndWordAndPartOfSpeech(Long userId, String word, String partOfSpeech);

    // 단어 검색
    List<Word> findByUserIdAndWordContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String keyword);

    // 단어 개수
    long countByUserId(Long userId);
}