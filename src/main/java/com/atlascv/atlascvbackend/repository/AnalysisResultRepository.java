package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.AnalysisResult;
import com.atlascv.atlascvbackend.entity.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByAnalysisRunIdOrderByFinalScoreDesc(UUID analysisRunId);

    @Modifying
    @Query("UPDATE AnalysisResult r SET r.status = :status WHERE r.id IN :ids")
    void bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") CandidateStatus status);

    @Query("""
        SELECT r FROM AnalysisResult r
        JOIN FETCH r.analysisRun run
        JOIN FETCH run.analysis a
        WHERE a.user.id = :userId
        AND run.createdAt = (
            SELECT MAX(run2.createdAt) FROM AnalysisRun run2
            WHERE run2.analysis.id = a.id
        )
        ORDER BY r.finalScore DESC
        """)
    List<AnalysisResult> findLatestRunResultsByUserId(@Param("userId") Long userId);
}
