package com.atlascv.atlascvbackend.repository;

import com.atlascv.atlascvbackend.entity.CandidateStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateStatusLogRepository extends JpaRepository<CandidateStatusLog, Long> {
    List<CandidateStatusLog> findByResultIdOrderByChangedAtAsc(Long resultId);
    void deleteByResultId(Long resultId);
}
