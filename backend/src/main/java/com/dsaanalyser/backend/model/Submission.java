package com.dsaanalyser.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter // Automatically creates getCode(), getId(), etc.
@Setter // Automatically creates setCode(), setScore(), etc.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;// [cite: 89]

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;// [cite: 89]

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    // --- THESE ARE THE NEW PROJECT-SPECIFIC FIELDS ---
    private Integer score; //[cite: 89]

    @Column(name = "complexity_detected")
    private String complexityDetected;// [cite: 89]

    @Column(columnDefinition = "TEXT")
    private String aiFeedback; //[cite: 62]

    @CreationTimestamp
    private LocalDateTime timestamp;// [cite: 89]
}