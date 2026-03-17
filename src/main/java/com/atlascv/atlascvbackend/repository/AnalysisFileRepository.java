package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisFileRepository extends JpaRepository<AnalysisFile, Long> {

    List<AnalysisFile> findByAnalysisId(Long analysisId);
}