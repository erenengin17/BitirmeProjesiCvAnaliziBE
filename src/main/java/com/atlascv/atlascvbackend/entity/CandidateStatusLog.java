package com.atlascv.atlascvbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_status_logs")
public class CandidateStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private AnalysisResult result;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus toStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    public void prePersist() {
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public AnalysisResult getResult() { return result; }
    public void setResult(AnalysisResult result) { this.result = result; }

    public CandidateStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(CandidateStatus fromStatus) { this.fromStatus = fromStatus; }

    public CandidateStatus getToStatus() { return toStatus; }
    public void setToStatus(CandidateStatus toStatus) { this.toStatus = toStatus; }

    public LocalDateTime getChangedAt() { return changedAt; }
}
