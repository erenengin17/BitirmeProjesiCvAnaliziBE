package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.repository.AnalysisRunRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AnalysisRunService {

    private final AnalysisRunRepository analysisRunRepository;

    public AnalysisRunService(AnalysisRunRepository analysisRunRepository) {
        this.analysisRunRepository = analysisRunRepository;
    }

    public AnalysisRun getLastRun(UUID analysisId) {
        return analysisRunRepository
                .findTopByAnalysisIdOrderByCreatedAtDesc(analysisId)
                .orElse(null);
    }
}