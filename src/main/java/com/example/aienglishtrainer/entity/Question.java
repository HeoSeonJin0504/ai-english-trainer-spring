package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 문제 모드: TOEIC, WRITING
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionMode mode;

    // 토익 파트: PART5, PART6, PART7 (토익만 해당)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ToeicPart toeicPart;

    // 영작 타입: SITUATION, TRANSLATION, FIX, SHORT_ANSWER (영작만 해당)
    @Column(length = 30)
    private String writingType;

    // 문제 주제
    @Column(nullable = false, length = 100)
    private String topic;

    // 지문 (Part6, Part7용)
    @Column(columnDefinition = "TEXT")
    private String passage;

    // 삽입 문장 (Part6용)
    @Column(length = 500)
    private String insertSentence;

    // 문제 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    // 선택지 (JSON 형태: {"A": "...", "B": "...", ...})
    @Column(columnDefinition = "TEXT")
    private String options;

    // 정답
    @Column(nullable = false, length = 500)
    private String answer;

    // 힌트 (영작용)
    @Column(length = 500)
    private String hint;

    // 해설
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}