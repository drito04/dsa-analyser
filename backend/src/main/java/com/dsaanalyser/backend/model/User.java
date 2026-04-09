package com.dsaanalyser.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// [cite: 82]

    private String name; //[cite: 83]
    private String email;// [cite: 83]

    @Column(name = "total_score")
    private Integer totalScore = 0; // Used for ranking users [cite: 83, 90]

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Submission> submissions;
}