package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "examples")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Example {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연결된 단어 (선택 - 없을 수도 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(nullable = false, length = 500)
    private String english;

    @Column(nullable = false, length = 500)
    private String korean;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 단어 연결
    public void linkWord(Word word) {
        this.word = word;
    }
}