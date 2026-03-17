package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByAnalysisRunIdOrderByFinalScoreDesc(Long analysisRunId);
}