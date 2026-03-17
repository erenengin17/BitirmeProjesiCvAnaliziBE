package com.atlascv.atlascvbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PythonAnalysisResponse {

    @JsonProperty("job_profile")
    private JobProfile jobProfile;

    @JsonProperty("total_candidates")
    private Integer totalCandidates;

    @JsonProperty("ranking")
    private List<PythonCandidateResult> ranking;

    public JobProfile getJobProfile() {
        return jobProfile;
    }

    public void setJobProfile(JobProfile jobProfile) {
        this.jobProfile = jobProfile;
    }

    public Integer getTotalCandidates() {
        return totalCandidates;
    }

    public void setTotalCandidates(Integer totalCandidates) {
        this.totalCandidates = totalCandidates;
    }

    public List<PythonCandidateResult> getRanking() {
        return ranking;
    }

    public void setRanking(List<PythonCandidateResult> ranking) {
        this.ranking = ranking;
    }

    public static class JobProfile {

        @JsonProperty("required_hard_skills")
        private List<String> requiredHardSkills;

        @JsonProperty("required_soft_skills")
        private List<String> requiredSoftSkills;

        @JsonProperty("required_education")
        private List<String> requiredEducation;

        @JsonProperty("min_experience_years")
        private Double minExperienceYears;

        @JsonProperty("require_project_or_certificate")
        private Boolean requireProjectOrCertificate;

        public List<String> getRequiredHardSkills() {
            return requiredHardSkills;
        }

        public void setRequiredHardSkills(List<String> requiredHardSkills) {
            this.requiredHardSkills = requiredHardSkills;
        }

        public List<String> getRequiredSoftSkills() {
            return requiredSoftSkills;
        }

        public void setRequiredSoftSkills(List<String> requiredSoftSkills) {
            this.requiredSoftSkills = requiredSoftSkills;
        }

        public List<String> getRequiredEducation() {
            return requiredEducation;
        }

        public void setRequiredEducation(List<String> requiredEducation) {
            this.requiredEducation = requiredEducation;
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
    }
}