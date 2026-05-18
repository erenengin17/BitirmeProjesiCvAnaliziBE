package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByAnalysisRunIdOrderByFinalScoreDesc(UUID analysisRunId);
}
