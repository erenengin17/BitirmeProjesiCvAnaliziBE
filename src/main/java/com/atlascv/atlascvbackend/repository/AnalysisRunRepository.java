package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisRunRepository extends JpaRepository<AnalysisRun, UUID> {

    List<AnalysisRun> findByAnalysisIdOrderByCreatedAtDesc(UUID analysisId);

    Optional<AnalysisRun> findTopByAnalysisIdOrderByCreatedAtDesc(UUID analysisId);
}
