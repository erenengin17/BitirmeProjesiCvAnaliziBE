package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.CreateAnalysisRequest;
import com.atlascv.atlascvbackend.entity.Analysis;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.AnalysisRepository;
import com.atlascv.atlascvbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;

    public AnalysisService(AnalysisRepository analysisRepository, UserRepository userRepository) {
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
    }

    public AnalysisResponse createAnalysis(CreateAnalysisRequest request) {
        if (request.getUserId() == null) {
            throw new RuntimeException("Kullanıcı ID zorunludur.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. ID: " + request.getUserId()));

        Analysis analysis = new Analysis();
        analysis.setAnalysisName(request.getAnalysisName());
        analysis.setPositionName(request.getPositionName());
        analysis.setDescription(request.getDescription());
        analysis.setCvCount(request.getCvCount() != null ? request.getCvCount() : 0);
        analysis.setStatus("Bekliyor");
        analysis.setUser(user);

        Analysis savedAnalysis = analysisRepository.save(analysis);
        return mapToResponse(savedAnalysis);
    }

    public List<AnalysisResponse> getUserAnalyses(Long userId) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AnalysisResponse> getRecentUserAnalyses(Long userId) {
        return analysisRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AnalysisResponse getAnalysisById(Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));

        return mapToResponse(analysis);
    }

    private AnalysisResponse mapToResponse(Analysis analysis) {
        AnalysisResponse response = new AnalysisResponse();
        response.setId(analysis.getId());
        response.setAnalysisName(analysis.getAnalysisName());
        response.setPositionName(analysis.getPositionName());
        response.setDescription(analysis.getDescription());
        response.setCvCount(analysis.getCvCount());
        response.setStatus(analysis.getStatus());
        response.setCreatedAt(analysis.getCreatedAt());
        response.setUserId(analysis.getUser().getId());
        response.setUserFullName(analysis.getUser().getFullName());
        return response;
    }
}