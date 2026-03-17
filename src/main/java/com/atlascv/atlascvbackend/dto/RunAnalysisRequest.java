package com.atlascv.atlascvbackend.dto;

import java.util.List;

public class RunAnalysisRequest {

    private String runName;
    private List<String> hardSkills;
    private List<String> softSkills;
    private List<String> educationRequirements;
    private Double minExperienceYears;
    private Boolean requireProjectOrCertificate;
    private Boolean useSemanticSimilarity;
    private List<String> extraKeywords;

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public List<String> getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(List<String> hardSkills) {
        this.hardSkills = hardSkills;
    }

    public List<String> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(List<String> softSkills) {
        this.softSkills = softSkills;
    }

    public List<String> getEducationRequirements() {
        return educationRequirements;
    }

    public void setEducationRequirements(List<String> educationRequirements) {
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

    public List<String> getExtraKeywords() {
        return extraKeywords;
    }

    public void setExtraKeywords(List<String> extraKeywords) {
        this.extraKeywords = extraKeywords;
    }
}