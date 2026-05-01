package com.atlascv.atlascvbackend.controller;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.RunAnalysisRequest;
import com.atlascv.atlascvbackend.entity.AnalysisFile;
import com.atlascv.atlascvbackend.entity.AnalysisResult;
import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.security.AnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analyses")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisResponse> createAnalysis(
            @RequestParam("analysisName") String analysisName,
            @RequestParam("positionName") String positionName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("userId") Long userId,
            @RequestPart("files") MultipartFile[] files
    ) {
        return ResponseEntity.ok(
                analysisService.createAnalysis(
                        analysisName,
                        positionName,
                        description,
                        userId,
                        files
                )
        );
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

    @PostMapping("/{analysisId}/run")
    public ResponseEntity<Map<String, Object>> runAnalysis(
            @PathVariable Long analysisId,
            @RequestBody RunAnalysisRequest request
    ) {
        Long runId = analysisService.runAnalysis(analysisId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("runId", runId);
        response.put("message", "Analiz çalıştırıldı");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/runs/{runId}/results")
    public ResponseEntity<List<AnalysisResult>> getRunResults(@PathVariable Long runId) {
        return ResponseEntity.ok(analysisService.getRunResults(runId));
    }

    @GetMapping("/{analysisId}/files")
    public ResponseEntity<List<AnalysisFile>> getAnalysisFiles(@PathVariable Long analysisId) {
        return ResponseEntity.ok(analysisService.getAnalysisFiles(analysisId));
    }
    @GetMapping("/{analysisId}/last-run")
    public ResponseEntity<AnalysisRun> getLastRun(@PathVariable Long analysisId) {
        AnalysisRun run = analysisService.getLastRun(analysisId);
        if (run == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(run);
    }

    @GetMapping("/files/{fileId}/content")
    public ResponseEntity<byte[]> getFileContent(@PathVariable Long fileId) {
        return analysisService.getFileContent(fileId);
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable Long analysisId) {
        analysisService.deleteAnalysis(analysisId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> updateAnalysis(
            @PathVariable Long analysisId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(analysisService.updateAnalysis(
                analysisId,
                body.get("analysisName"),
                body.get("positionName"),
                body.get("description")
        ));
    }

    @GetMapping("/{analysisId}/runs")
    public ResponseEntity<List<AnalysisRun>> getAnalysisRuns(@PathVariable Long analysisId) {
        return ResponseEntity.ok(analysisService.getAnalysisRuns(analysisId));
    }

    @PutMapping("/results/{resultId}/note")
    public ResponseEntity<Void> updateResultNote(
            @PathVariable Long resultId,
            @RequestBody Map<String, String> body
    ) {
        analysisService.updateResultNote(resultId, body.getOrDefault("note", ""));
        return ResponseEntity.ok().build();
    }
}