package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vocabularies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단어 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 단어
    @Column(nullable = false, length = 100)
    private String word;

    // 품사 및 의미 (JSON 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String meanings;

    // 예문 (JSON 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String examples;

    // 유의어 (JSON 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String synonym;

    // 반의어 (JSON 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String antonym;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 단어 정보 수정
    public void update(String meanings, String examples, String synonym, String antonym) {
        if (meanings != null) this.meanings = meanings;
        if (examples != null) this.examples = examples;
        if (synonym != null) this.synonym = synonym;
        if (antonym != null) this.antonym = antonym;
    }
}