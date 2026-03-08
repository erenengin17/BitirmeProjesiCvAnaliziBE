package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    List<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Analysis> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}