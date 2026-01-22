package com.example.aienglishtrainer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "words", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "word", "part_of_speech"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(name = "part_of_speech", nullable = false, length = 50)
    private String partOfSpeech;

    @Column(nullable = false, length = 255)
    private String meaning;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}