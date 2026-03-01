package com.fij.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String company;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String jobType;
    private String workSchedule;
    private String remotePolicy;
    private Integer duration;
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private User recruiter;
}
