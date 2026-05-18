package com.atlascv.atlascvbackend.controller;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.RunAnalysisRequest;
import com.atlascv.atlascvbackend.entity.AnalysisFile;
import com.atlascv.atlascvbackend.entity.AnalysisResult;
import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.security.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Analiz", description = "CV analizi oluşturma, çalıştırma ve sonuç yönetimi")
@SecurityRequirement(name = "bearerAuth")
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
            @RequestPart("files") MultipartFile[] files,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        return ResponseEntity.ok(
                analysisService.createAnalysis(analysisName, positionName, description, userId, files)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<AnalysisResponse>> getUserAnalyses(Authentication authentication) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        return ResponseEntity.ok(analysisService.getUserAnalyses(userId));
    }

    @GetMapping("/my/recent")
    public ResponseEntity<List<AnalysisResponse>> getRecentUserAnalyses(Authentication authentication) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        return ResponseEntity.ok(analysisService.getRecentUserAnalyses(userId));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> getAnalysisById(
            @PathVariable UUID analysisId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        return ResponseEntity.ok(analysisService.getAnalysisById(analysisId));
    }

    @PostMapping("/{analysisId}/run")
    public ResponseEntity<Map<String, Object>> runAnalysis(
            @PathVariable UUID analysisId,
            @RequestBody RunAnalysisRequest request,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        UUID runId = analysisService.runAnalysis(analysisId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("runId", runId);
        response.put("message", "Analiz çalıştırıldı");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/runs/{runId}/results")
    public ResponseEntity<List<AnalysisResult>> getRunResults(
            @PathVariable UUID runId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertRunOwner(runId, userId);
        return ResponseEntity.ok(analysisService.getRunResults(runId));
    }

    @GetMapping("/{analysisId}/files")
    public ResponseEntity<List<AnalysisFile>> getAnalysisFiles(
            @PathVariable UUID analysisId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        return ResponseEntity.ok(analysisService.getAnalysisFiles(analysisId));
    }

    @GetMapping("/{analysisId}/last-run")
    public ResponseEntity<AnalysisRun> getLastRun(
            @PathVariable UUID analysisId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        AnalysisRun run = analysisService.getLastRun(analysisId);
        if (run == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(run);
    }

    @GetMapping("/files/{fileId}/content")
    public ResponseEntity<byte[]> getFileContent(
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertFileOwner(fileId, userId);
        return analysisService.getFileContent(fileId);
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable UUID analysisId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        analysisService.deleteAnalysis(analysisId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> updateAnalysis(
            @PathVariable UUID analysisId,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        return ResponseEntity.ok(analysisService.updateAnalysis(
                analysisId,
                body.get("analysisName"),
                body.get("positionName"),
                body.get("description")
        ));
    }

    @GetMapping("/{analysisId}/runs")
    public ResponseEntity<List<AnalysisRun>> getAnalysisRuns(
            @PathVariable UUID analysisId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        return ResponseEntity.ok(analysisService.getAnalysisRuns(analysisId));
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<AnalysisRun> getRunById(
            @PathVariable UUID runId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertRunOwner(runId, userId);
        return ResponseEntity.ok(analysisService.getRunById(runId));
    }

    @PostMapping("/results/{resultId}/explain")
    public ResponseEntity<Map<String, String>> explainResult(
            @PathVariable Long resultId,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertResultOwner(resultId, userId);
        String explanation = analysisService.explainResult(resultId);
        Map<String, String> response = new HashMap<>();
        response.put("explanation", explanation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{analysisId}/clone")
    public ResponseEntity<AnalysisResponse> cloneAnalysis(
            @PathVariable UUID analysisId,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertAnalysisOwner(analysisId, userId);
        String newName = body != null ? body.get("newName") : null;
        return ResponseEntity.ok(analysisService.cloneAnalysis(analysisId, userId, newName));
    }

    @PutMapping("/results/{resultId}/note")
    public ResponseEntity<Void> updateResultNote(
            @PathVariable Long resultId,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        Long userId = analysisService.getUserIdByEmail(authentication.getName());
        analysisService.assertResultOwner(resultId, userId);
        analysisService.updateResultNote(resultId, body.getOrDefault("note", ""));
        return ResponseEntity.ok().build();
    }
}
