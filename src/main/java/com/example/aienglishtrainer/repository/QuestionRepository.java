package com.example.aienglishtrainer.repository;

import com.example.aienglishtrainer.entity.Question;
import com.example.aienglishtrainer.entity.QuestionMode;
import com.example.aienglishtrainer.entity.ToeicPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 사용자의 모든 문제 조회 (최신순)
    List<Question> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자의 특정 문제 조회
    Optional<Question> findByIdAndUserId(Long id, Long userId);

    // 모드별 문제 조회 (토익 or 영작)
    List<Question> findByUserIdAndModeOrderByCreatedAtDesc(Long userId, QuestionMode mode);

    // 토익 파트별 문제 조회
    List<Question> findByUserIdAndModeAndToeicPartOrderByCreatedAtDesc(Long userId, QuestionMode mode, ToeicPart toeicPart);

    // 주제별 문제 조회
    List<Question> findByUserIdAndTopicContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String topic);

    // 문제 개수
    long countByUserId(Long userId);

    // 모드별 문제 개수
    long countByUserIdAndMode(Long userId, QuestionMode mode);
}