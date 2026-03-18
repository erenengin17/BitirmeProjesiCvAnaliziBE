package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRunRepository extends JpaRepository<AnalysisRun, Long> {

    List<AnalysisRun> findByAnalysisIdOrderByCreatedAtDesc(Long analysisId);

    Optional<AnalysisRun> findTopByAnalysisIdOrderByCreatedAtDesc(Long analysisId);
}