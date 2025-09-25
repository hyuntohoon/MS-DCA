package dca.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_seq")
    private Long reviewSeq;   // bigint → Long

    @Column(columnDefinition = "LONGTEXT")
    private String content;   // longtext

    @Column(name = "created_at")
    private LocalDateTime createdAt; // datetime

    @Column(name = "is_active", nullable = false)
    private Boolean isActive; // bit(1) → Boolean 매핑

    @Column(name = "is_spoiler")
    private Boolean isSpoiler; // tinyint(1) → Boolean 매핑

    @Column
    private Integer likes; // int

    @Column(name = "movie_seq", nullable = false)
    private Integer movieSeq; // int (FK, 지금은 단순히 값만 매핑)

    @Column(name = "review_rating")
    private Double reviewRating; // double

    @Column(name = "sentiment_score")
    private Double sentimentScore; // double

    // =========================
    // 관계 매핑
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private User user; // FK (user_seq)
}