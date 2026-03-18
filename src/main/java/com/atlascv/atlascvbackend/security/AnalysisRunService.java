package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.repository.AnalysisRunRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalysisRunService {

    private final AnalysisRunRepository analysisRunRepository;

    public AnalysisRunService(AnalysisRunRepository analysisRunRepository) {
        this.analysisRunRepository = analysisRunRepository;
    }

    public AnalysisRun getLastRun(Long analysisId) {
        return analysisRunRepository
                .findTopByAnalysisIdOrderByCreatedAtDesc(analysisId)
                .orElse(null);
    }
}