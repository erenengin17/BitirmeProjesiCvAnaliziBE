package com.atlascv.atlascvbackend.dto;

import java.time.LocalDateTime;

public class AnalysisResponse {

    private Long id;
    private String analysisName;
    private String positionName;
    private String description;
    private Integer cvCount;
    private String status;
    private LocalDateTime createdAt;
    private Long userId;
    private String userFullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}