package com.atlascv.atlascvbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;

    private String fileName;

    private Double finalScore;
    private Double hardSkillScore;
    private Double softSkillScore;
    private Double educationScore;
    private Double experienceScore;
    private Double projectCertScore;
    private Double semanticScore;

    @Column(columnDefinition = "TEXT")
    private String matchedHardSkills;

    @Column(columnDefinition = "TEXT")
    private String missingHardSkills;

    @Column(columnDefinition = "TEXT")
    private String matchedSoftSkills;

    @Column(columnDefinition = "TEXT")
    private String matchedEducation;

    @Column(columnDefinition = "TEXT")
    private String candidateHardSkills;

    @Column(columnDefinition = "TEXT")
    private String candidateSoftSkills;

    @Column(columnDefinition = "TEXT")
    private String candidateEducation;

    @Column(columnDefinition = "TEXT")
    private String candidateCertificates;

    @Column(columnDefinition = "TEXT")
    private String candidateProjects;

    private Double experienceYears;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_run_id", nullable = false)
    private AnalysisRun analysisRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_file_id")
    private AnalysisFile analysisFile;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getHardSkillScore() {
        return hardSkillScore;
    }

    public void setHardSkillScore(Double hardSkillScore) {
        this.hardSkillScore = hardSkillScore;
    }

    public Double getSoftSkillScore() {
        return softSkillScore;
    }

    public void setSoftSkillScore(Double softSkillScore) {
        this.softSkillScore = softSkillScore;
    }

    public Double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }

    public Double getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(Double experienceScore) {
        this.experienceScore = experienceScore;
    }

    public Double getProjectCertScore() {
        return projectCertScore;
    }

    public void setProjectCertScore(Double projectCertScore) {
        this.projectCertScore = projectCertScore;
    }

    public Double getSemanticScore() {
        return semanticScore;
    }

    public void setSemanticScore(Double semanticScore) {
        this.semanticScore = semanticScore;
    }

    public String getMatchedHardSkills() {
        return matchedHardSkills;
    }

    public void setMatchedHardSkills(String matchedHardSkills) {
        this.matchedHardSkills = matchedHardSkills;
    }

    public String getMissingHardSkills() {
        return missingHardSkills;
    }

    public void setMissingHardSkills(String missingHardSkills) {
        this.missingHardSkills = missingHardSkills;
    }

    public String getMatchedSoftSkills() {
        return matchedSoftSkills;
    }

    public void setMatchedSoftSkills(String matchedSoftSkills) {
        this.matchedSoftSkills = matchedSoftSkills;
    }

    public String getMatchedEducation() {
        return matchedEducation;
    }

    public void setMatchedEducation(String matchedEducation) {
        this.matchedEducation = matchedEducation;
    }

    public String getCandidateHardSkills() {
        return candidateHardSkills;
    }

    public void setCandidateHardSkills(String candidateHardSkills) {
        this.candidateHardSkills = candidateHardSkills;
    }

    public String getCandidateSoftSkills() {
        return candidateSoftSkills;
    }

    public void setCandidateSoftSkills(String candidateSoftSkills) {
        this.candidateSoftSkills = candidateSoftSkills;
    }

    public String getCandidateEducation() {
        return candidateEducation;
    }

    public void setCandidateEducation(String candidateEducation) {
        this.candidateEducation = candidateEducation;
    }

    public String getCandidateCertificates() {
        return candidateCertificates;
    }

    public void setCandidateCertificates(String candidateCertificates) {
        this.candidateCertificates = candidateCertificates;
    }

    public String getCandidateProjects() {
        return candidateProjects;
    }

    public void setCandidateProjects(String candidateProjects) {
        this.candidateProjects = candidateProjects;
    }

    public Double getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Double experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AnalysisRun getAnalysisRun() {
        return analysisRun;
    }

    public void setAnalysisRun(AnalysisRun analysisRun) {
        this.analysisRun = analysisRun;
    }

    @JsonIgnore
    public AnalysisFile getAnalysisFile() {
        return analysisFile;
    }

    public void setAnalysisFile(AnalysisFile analysisFile) {
        this.analysisFile = analysisFile;
    }

    @JsonProperty("analysisFileId")
    public Long getAnalysisFileId() {
        return analysisFile != null ? analysisFile.getId() : null;
    }
}