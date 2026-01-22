package com.example.aienglishtrainer.repository;

import com.example.aienglishtrainer.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {

    // 사용자의 모든 예문 조회 (최신순)
    List<Example> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자의 특정 예문 조회
    Optional<Example> findByIdAndUserId(Long id, Long userId);

    // 특정 단어의 예문 조회
    List<Example> findByWordIdOrderByCreatedAtDesc(Long wordId);

    // 사용자의 특정 단어 예문 조회
    List<Example> findByUserIdAndWordIdOrderByCreatedAtDesc(Long userId, Long wordId);

    // 중복 확인
    boolean existsByUserIdAndEnglish(Long userId, String english);

    // 예문 검색
    List<Example> findByUserIdAndEnglishContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String keyword);

    // 예문 개수
    long countByUserId(Long userId);
}