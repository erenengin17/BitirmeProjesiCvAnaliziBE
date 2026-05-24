package com.atlascv.atlascvbackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PipelineResultDto {
    private Long id;
    private String candidateName;
    private String fileName;
    private double finalScore;
    private String status;
    private String matchedHardSkills;
    private String missingHardSkills;
    private String matchedSoftSkills;
    private double experienceYears;
    private String summary;
    private String note;
    private String analysisName;
    private String positionName;
    private UUID analysisId;
    private UUID runId;
    private Long analysisFileId;
    private String candidateEmail;
    private LocalDateTime analysisCreatedAt;
    private LocalDateTime interviewDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMatchedHardSkills() { return matchedHardSkills; }
    public void setMatchedHardSkills(String matchedHardSkills) { this.matchedHardSkills = matchedHardSkills; }

    public String getMissingHardSkills() { return missingHardSkills; }
    public void setMissingHardSkills(String missingHardSkills) { this.missingHardSkills = missingHardSkills; }

    public String getMatchedSoftSkills() { return matchedSoftSkills; }
    public void setMatchedSoftSkills(String matchedSoftSkills) { this.matchedSoftSkills = matchedSoftSkills; }

    public double getExperienceYears() { return experienceYears; }
    public void setExperienceYears(double experienceYears) { this.experienceYears = experienceYears; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAnalysisName() { return analysisName; }
    public void setAnalysisName(String analysisName) { this.analysisName = analysisName; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public UUID getAnalysisId() { return analysisId; }
    public void setAnalysisId(UUID analysisId) { this.analysisId = analysisId; }

    public UUID getRunId() { return runId; }
    public void setRunId(UUID runId) { this.runId = runId; }

    public Long getAnalysisFileId() { return analysisFileId; }
    public void setAnalysisFileId(Long analysisFileId) { this.analysisFileId = analysisFileId; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public LocalDateTime getAnalysisCreatedAt() { return analysisCreatedAt; }
    public void setAnalysisCreatedAt(LocalDateTime analysisCreatedAt) { this.analysisCreatedAt = analysisCreatedAt; }

    public LocalDateTime getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDateTime interviewDate) { this.interviewDate = interviewDate; }
}
