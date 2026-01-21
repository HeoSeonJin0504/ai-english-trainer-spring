package com.example.aienglishtrainer.repository;

import com.example.aienglishtrainer.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    // 사용자의 모든 단어 조회 (최신순)
    List<Vocabulary> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자의 특정 단어 조회
    Optional<Vocabulary> findByIdAndUserId(Long id, Long userId);

    // 사용자가 해당 단어를 이미 저장했는지 확인
    boolean existsByUserIdAndWord(Long userId, String word);

    // 사용자의 단어 검색 (단어에 포함된 것)
    List<Vocabulary> findByUserIdAndWordContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String keyword);

    // 사용자의 단어 개수
    long countByUserId(Long userId);
}