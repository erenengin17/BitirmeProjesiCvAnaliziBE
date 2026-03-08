package com.atlascv.atlascvbackend.controller;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.CreateAnalysisRequest;
import com.atlascv.atlascvbackend.security.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse> createAnalysis(@RequestBody CreateAnalysisRequest request) {
        return ResponseEntity.ok(analysisService.createAnalysis(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalysisResponse>> getUserAnalyses(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getUserAnalyses(userId));
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<AnalysisResponse>> getRecentUserAnalyses(@PathVariable Long userId) {
        return ResponseEntity.ok(analysisService.getRecentUserAnalyses(userId));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> getAnalysisById(@PathVariable Long analysisId) {
        return ResponseEntity.ok(analysisService.getAnalysisById(analysisId));
    }
}