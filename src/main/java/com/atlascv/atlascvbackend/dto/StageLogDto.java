package com.atlascv.atlascvbackend.dto;

import java.time.LocalDateTime;

public class StageLogDto {
    private Long id;
    private String fromStatus;
    private String toStatus;
    private LocalDateTime changedAt;

    public StageLogDto(Long id, String fromStatus, String toStatus, LocalDateTime changedAt) {
        this.id = id;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
    }

    public Long getId() { return id; }
    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
