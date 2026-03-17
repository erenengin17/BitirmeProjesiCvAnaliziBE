package com.atlascv.atlascvbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PythonCandidateResult {

    @JsonProperty("candidate_name")
    private String candidateName;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("hard_skill_score")
    private Double hardSkillScore;

    @JsonProperty("soft_skill_score")
    private Double softSkillScore;

    @JsonProperty("education_score")
    private Double educationScore;

    @JsonProperty("experience_score")
    private Double experienceScore;

    @JsonProperty("project_cert_score")
    private Double projectCertScore;

    @JsonProperty("semantic_score")
    private Double semanticScore;

    @JsonProperty("final_score")
    private Double finalScore;

    @JsonProperty("matched_hard_skills")
    private List<String> matchedHardSkills;

    @JsonProperty("missing_hard_skills")
    private List<String> missingHardSkills;

    @JsonProperty("matched_soft_skills")
    private List<String> matchedSoftSkills;

    @JsonProperty("matched_education")
    private List<String> matchedEducation;

    @JsonProperty("candidate_hard_skills")
    private List<String> candidateHardSkills;

    @JsonProperty("candidate_soft_skills")
    private List<String> candidateSoftSkills;

    @JsonProperty("candidate_education")
    private List<String> candidateEducation;

    @JsonProperty("candidate_certificates")
    private List<String> candidateCertificates;

    @JsonProperty("candidate_projects")
    private List<String> candidateProjects;

    @JsonProperty("experience_years")
    private Double experienceYears;

    @JsonProperty("summary")
    private String summary;

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

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public List<String> getMatchedHardSkills() {
        return matchedHardSkills;
    }

    public void setMatchedHardSkills(List<String> matchedHardSkills) {
        this.matchedHardSkills = matchedHardSkills;
    }

    public List<String> getMissingHardSkills() {
        return missingHardSkills;
    }

    public void setMissingHardSkills(List<String> missingHardSkills) {
        this.missingHardSkills = missingHardSkills;
    }

    public List<String> getMatchedSoftSkills() {
        return matchedSoftSkills;
    }

    public void setMatchedSoftSkills(List<String> matchedSoftSkills) {
        this.matchedSoftSkills = matchedSoftSkills;
    }

    public List<String> getMatchedEducation() {
        return matchedEducation;
    }

    public void setMatchedEducation(List<String> matchedEducation) {
        this.matchedEducation = matchedEducation;
    }

    public List<String> getCandidateHardSkills() {
        return candidateHardSkills;
    }

    public void setCandidateHardSkills(List<String> candidateHardSkills) {
        this.candidateHardSkills = candidateHardSkills;
    }

    public List<String> getCandidateSoftSkills() {
        return candidateSoftSkills;
    }

    public void setCandidateSoftSkills(List<String> candidateSoftSkills) {
        this.candidateSoftSkills = candidateSoftSkills;
    }

    public List<String> getCandidateEducation() {
        return candidateEducation;
    }

    public void setCandidateEducation(List<String> candidateEducation) {
        this.candidateEducation = candidateEducation;
    }

    public List<String> getCandidateCertificates() {
        return candidateCertificates;
    }

    public void setCandidateCertificates(List<String> candidateCertificates) {
        this.candidateCertificates = candidateCertificates;
    }

    public List<String> getCandidateProjects() {
        return candidateProjects;
    }

    public void setCandidateProjects(List<String> candidateProjects) {
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
}