package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    List<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Analysis> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
