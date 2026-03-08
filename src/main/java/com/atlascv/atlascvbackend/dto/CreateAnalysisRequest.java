package com.atlascv.atlascvbackend.dto;

public class CreateAnalysisRequest {

    private String analysisName;
    private String positionName;
    private String description;
    private Integer cvCount;
    private Long userId;

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCvCount() {
        return cvCount;
    }

    public void setCvCount(Integer cvCount) {
        this.cvCount = cvCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}