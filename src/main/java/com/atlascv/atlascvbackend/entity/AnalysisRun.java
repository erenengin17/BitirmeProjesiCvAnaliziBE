package com.atlascv.atlascvbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_runs")
public class AnalysisRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String runName;

    @Column(columnDefinition = "TEXT")
    private String hardSkills;

    @Column(columnDefinition = "TEXT")
    private String softSkills;

    @Column(columnDefinition = "TEXT")
    private String educationRequirements;

    private Double minExperienceYears;

    private Boolean requireProjectOrCertificate;

    private Boolean useSemanticSimilarity;

    @Column(columnDefinition = "TEXT")
    private String extraKeywords;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public String getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(String hardSkills) {
        this.hardSkills = hardSkills;
    }

    public String getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(String softSkills) {
        this.softSkills = softSkills;
    }

    public String getEducationRequirements() {
        return educationRequirements;
    }

    public void setEducationRequirements(String educationRequirements) {
        this.educationRequirements = educationRequirements;
    }

    public Double getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Double minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public Boolean getRequireProjectOrCertificate() {
        return requireProjectOrCertificate;
    }

    public void setRequireProjectOrCertificate(Boolean requireProjectOrCertificate) {
        this.requireProjectOrCertificate = requireProjectOrCertificate;
    }

    public Boolean getUseSemanticSimilarity() {
        return useSemanticSimilarity;
    }

    public void setUseSemanticSimilarity(Boolean useSemanticSimilarity) {
        this.useSemanticSimilarity = useSemanticSimilarity;
    }

    public String getExtraKeywords() {
        return extraKeywords;
    }

    public void setExtraKeywords(String extraKeywords) {
        this.extraKeywords = extraKeywords;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }
}