package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.PipelineResultDto;
import com.atlascv.atlascvbackend.dto.PythonAnalysisResponse;
import com.atlascv.atlascvbackend.dto.PythonCandidateResult;
import com.atlascv.atlascvbackend.dto.RunAnalysisRequest;
import com.atlascv.atlascvbackend.dto.StageLogDto;
import com.atlascv.atlascvbackend.entity.Analysis;
import com.atlascv.atlascvbackend.entity.AnalysisFile;
import com.atlascv.atlascvbackend.entity.AnalysisResult;
import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.entity.CandidateStatusLog;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.AnalysisFileRepository;
import com.atlascv.atlascvbackend.repository.AnalysisRepository;
import com.atlascv.atlascvbackend.repository.AnalysisResultRepository;
import com.atlascv.atlascvbackend.repository.AnalysisRunRepository;
import com.atlascv.atlascvbackend.repository.CandidateStatusLogRepository;
import com.atlascv.atlascvbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final AnalysisFileRepository analysisFileRepository;
    private final AnalysisRunRepository analysisRunRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final CandidateStatusLogRepository statusLogRepository;
    private final FileStorageService fileStorageService;
    private final PythonAnalysisService pythonAnalysisService;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            UserRepository userRepository,
            AnalysisFileRepository analysisFileRepository,
            AnalysisRunRepository analysisRunRepository,
            AnalysisResultRepository analysisResultRepository,
            CandidateStatusLogRepository statusLogRepository,
            FileStorageService fileStorageService,
            PythonAnalysisService pythonAnalysisService
    ) {
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
        this.analysisFileRepository = analysisFileRepository;
        this.analysisRunRepository = analysisRunRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.statusLogRepository = statusLogRepository;
        this.fileStorageService = fileStorageService;
        this.pythonAnalysisService = pythonAnalysisService;
    }

    @Transactional
    public AnalysisResponse createAnalysis(
            String analysisName,
            String positionName,
            String description,
            Long userId,
            MultipartFile[] files
    ) {
        if (userId == null) {
            throw new RuntimeException("Kullanıcı ID zorunludur.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. ID: " + userId));

        Analysis analysis = new Analysis();
        analysis.setAnalysisName(analysisName);
        analysis.setPositionName(positionName);
        analysis.setDescription(description);
        if (files != null && files.length > 100) {
            throw new RuntimeException("Maksimum 100 CV yükleyebilirsiniz.");
        }
        analysis.setCvCount(files != null ? files.length : 0);
        analysis.setStatus("Bekliyor");
        analysis.setUser(user);

        Analysis savedAnalysis = analysisRepository.save(analysis);

        if (files != null) {
            for (MultipartFile file : files) {
                String savedPath = fileStorageService.storeFile(savedAnalysis.getId(), file);

                AnalysisFile analysisFile = new AnalysisFile();
                analysisFile.setAnalysis(savedAnalysis);
                String origName = file.getOriginalFilename();
                analysisFile.setOriginalFileName(origName != null ? origName : "unknown.pdf");
                analysisFile.setStoredFileName(Paths.get(savedPath).getFileName().toString());
                analysisFile.setFilePath(savedPath);
                analysisFile.setFileSize(file.getSize());
                analysisFile.setMimeType(file.getContentType());

                analysisFileRepository.save(analysisFile);
            }
        }

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

    public AnalysisResponse getAnalysisById(UUID analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));

        return mapToResponse(analysis);
    }

    public UUID runAnalysis(UUID analysisId, RunAnalysisRequest request) {
        System.out.println("RUN başladı. analysisId = " + analysisId);

        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));
        System.out.println("analysis bulundu: " + analysis.getAnalysisName());

        List<AnalysisFile> files = analysisFileRepository.findByAnalysisId(analysisId);
        System.out.println("dosya sayısı: " + files.size());

        if (files.isEmpty()) {
            throw new RuntimeException("Bu analize ait CV dosyası bulunamadı.");
        }

        AnalysisRun analysisRun = new AnalysisRun();
        analysisRun.setAnalysis(analysis);
        analysisRun.setRunName(request.getRunName() != null ? request.getRunName() : "Varsayılan Analiz");
        analysisRun.setHardSkills(joinList(request.getHardSkills()));
        analysisRun.setSoftSkills(joinList(request.getSoftSkills()));
        analysisRun.setEducationRequirements(joinList(request.getEducationRequirements()));
        analysisRun.setMinExperienceYears(request.getMinExperienceYears() != null ? request.getMinExperienceYears() : 0.0);
        analysisRun.setRequireProjectOrCertificate(
                request.getRequireProjectOrCertificate() != null ? request.getRequireProjectOrCertificate() : false
        );
        analysisRun.setUseSemanticSimilarity(
                request.getUseSemanticSimilarity() != null ? request.getUseSemanticSimilarity() : true
        );
        analysisRun.setExtraKeywords(joinList(request.getExtraKeywords()));

        AnalysisRun savedRun = analysisRunRepository.save(analysisRun);
        System.out.println("run kaydedildi: " + savedRun.getId());

        System.out.println("Python servisine istek gönderiliyor...");
        PythonAnalysisResponse pythonResponse = pythonAnalysisService.runAnalysis(
                analysis.getDescription(),
                joinList(request.getHardSkills()),
                joinList(request.getSoftSkills()),
                joinList(request.getEducationRequirements()),
                request.getMinExperienceYears(),
                request.getRequireProjectOrCertificate(),
                request.getUseSemanticSimilarity(),
                files
        );
        System.out.println("Python cevabı geldi.");

        if (pythonResponse == null) {
            System.out.println("pythonResponse NULL geldi!");
            throw new RuntimeException("Python cevabı boş geldi.");
        }

        if (pythonResponse.getRanking() == null) {
            System.out.println("pythonResponse.getRanking() NULL!");
            throw new RuntimeException("Python ranking bilgisi boş geldi.");
        }

        System.out.println("ranking size: " + pythonResponse.getRanking().size());

        Map<String, AnalysisFile> fileMap = files.stream()
                .collect(Collectors.toMap(AnalysisFile::getOriginalFileName, f -> f, (a, b) -> a));

        for (PythonCandidateResult item : pythonResponse.getRanking()) {
            System.out.println("Result kaydediliyor: " + item.getCandidateName());

            AnalysisResult result = new AnalysisResult();
            result.setAnalysisRun(savedRun);

            AnalysisFile matchedFile = fileMap.get(item.getFileName());
            result.setAnalysisFile(matchedFile);

            result.setCandidateName(item.getCandidateName());
            result.setFileName(item.getFileName());
            result.setFinalScore(item.getFinalScore());
            result.setHardSkillScore(item.getHardSkillScore());
            result.setSoftSkillScore(item.getSoftSkillScore());
            result.setEducationScore(item.getEducationScore());
            result.setExperienceScore(item.getExperienceScore());
            result.setProjectCertScore(item.getProjectCertScore());
            result.setSemanticScore(item.getSemanticScore());

            result.setMatchedHardSkills(joinList(item.getMatchedHardSkills()));
            result.setMissingHardSkills(joinList(item.getMissingHardSkills()));
            result.setMatchedSoftSkills(joinList(item.getMatchedSoftSkills()));
            result.setMatchedEducation(joinList(item.getMatchedEducation()));
            result.setCandidateHardSkills(joinList(item.getCandidateHardSkills()));
            result.setCandidateSoftSkills(joinList(item.getCandidateSoftSkills()));
            result.setCandidateEducation(joinList(item.getCandidateEducation()));
            result.setCandidateCertificates(joinList(item.getCandidateCertificates()));
            result.setCandidateProjects(joinList(item.getCandidateProjects()));
            result.setExperienceYears(item.getExperienceYears());
            result.setSummary(item.getSummary());
            result.setCandidateEmail(item.getCandidateEmail());

            analysisResultRepository.save(result);
            System.out.println("Result DB'ye yazıldı: " + item.getCandidateName());
        }

        analysis.setStatus("Tamamlandi");
        analysisRepository.save(analysis);

        System.out.println("RUN başarıyla tamamlandı. runId = " + savedRun.getId());
        return savedRun.getId();
    }

    public List<AnalysisResult> getRunResults(UUID runId) {
        return analysisResultRepository.findByAnalysisRunIdOrderByFinalScoreDesc(runId);
    }

    public List<AnalysisFile> getAnalysisFiles(UUID analysisId) {
        return analysisFileRepository.findByAnalysisId(analysisId);
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

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join(",", values);
    }
    @Transactional
    public AnalysisResponse cloneAnalysis(UUID sourceAnalysisId, Long userId, String newName) {
        Analysis source = analysisRepository.findById(sourceAnalysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + sourceAnalysisId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. ID: " + userId));

        List<AnalysisFile> sourceFiles = analysisFileRepository.findByAnalysisId(sourceAnalysisId);

        Analysis clone = new Analysis();
        clone.setAnalysisName((newName != null && !newName.isBlank()) ? newName : source.getAnalysisName() + " (Kopya)");
        clone.setPositionName(source.getPositionName());
        clone.setDescription(source.getDescription());
        clone.setCvCount(sourceFiles.size());
        clone.setStatus("Bekliyor");
        clone.setUser(user);

        Analysis savedClone = analysisRepository.save(clone);

        for (AnalysisFile sourceFile : sourceFiles) {
            AnalysisFile newFile = new AnalysisFile();
            newFile.setAnalysis(savedClone);
            newFile.setOriginalFileName(sourceFile.getOriginalFileName());
            newFile.setStoredFileName(sourceFile.getStoredFileName());
            newFile.setFilePath(sourceFile.getFilePath());
            newFile.setFileSize(sourceFile.getFileSize());
            newFile.setMimeType(sourceFile.getMimeType());
            analysisFileRepository.save(newFile);
        }

        return mapToResponse(savedClone);
    }

    @Transactional
    public void deleteAnalysis(UUID analysisId) {
        List<AnalysisRun> runs = analysisRunRepository.findByAnalysisIdOrderByCreatedAtDesc(analysisId);
        for (AnalysisRun run : runs) {
            List<AnalysisResult> results = analysisResultRepository.findByAnalysisRunIdOrderByFinalScoreDesc(run.getId());
            for (AnalysisResult result : results) {
                statusLogRepository.deleteByResultId(result.getId());
            }
            analysisResultRepository.deleteAll(results);
        }
        analysisRunRepository.deleteAll(runs);

        List<AnalysisFile> files = analysisFileRepository.findByAnalysisId(analysisId);
        for (AnalysisFile file : files) {
            try { Files.deleteIfExists(Paths.get(file.getFilePath())); } catch (IOException ignored) {}
        }
        analysisFileRepository.deleteAll(files);

        analysisRepository.deleteById(analysisId);
    }

    @Transactional
    public AnalysisResponse updateAnalysis(UUID analysisId, String analysisName, String positionName, String description) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));
        if (analysisName != null && !analysisName.isBlank()) analysis.setAnalysisName(analysisName);
        if (positionName != null && !positionName.isBlank()) analysis.setPositionName(positionName);
        analysis.setDescription(description);
        return mapToResponse(analysisRepository.save(analysis));
    }

    public List<AnalysisRun> getAnalysisRuns(UUID analysisId) {
        return analysisRunRepository.findByAnalysisIdOrderByCreatedAtDesc(analysisId);
    }

    public AnalysisRun getLastRun(UUID analysisId) {
        return analysisRunRepository
                .findTopByAnalysisIdOrderByCreatedAtDesc(analysisId)
                .orElse(null);
    }

    public AnalysisRun getRunById(UUID runId) {
        return analysisRunRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Çalıştırma bulunamadı. ID: " + runId));
    }

    @Transactional
    public String explainResult(Long resultId) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));

        AnalysisRun run = result.getAnalysisRun();

        Map<String, Object> body = new HashMap<>();
        body.put("candidateName",         result.getCandidateName());
        body.put("finalScore",            result.getFinalScore());
        body.put("hardSkillScore",        result.getHardSkillScore());
        body.put("softSkillScore",        result.getSoftSkillScore());
        body.put("educationScore",        result.getEducationScore());
        body.put("experienceScore",       result.getExperienceScore());
        body.put("projectCertScore",      result.getProjectCertScore());
        body.put("semanticScore",         result.getSemanticScore());
        body.put("matchedHardSkills",     splitToList(result.getMatchedHardSkills()));
        body.put("missingHardSkills",     splitToList(result.getMissingHardSkills()));
        body.put("matchedSoftSkills",     splitToList(result.getMatchedSoftSkills()));
        body.put("candidateHardSkills",   splitToList(result.getCandidateHardSkills()));
        body.put("candidateEducation",    result.getCandidateEducation());
        body.put("candidateProjects",     splitToList(result.getCandidateProjects()));
        body.put("candidateCertificates", splitToList(result.getCandidateCertificates()));
        body.put("experienceYears",       result.getExperienceYears());
        body.put("requiredHardSkills",    splitToList(run.getHardSkills()));
        body.put("requiredSoftSkills",    splitToList(run.getSoftSkills()));
        body.put("requiredEducation",     splitToList(run.getEducationRequirements()));
        body.put("minExperienceYears",    run.getMinExperienceYears());

        return pythonAnalysisService.callExplain(body);
    }

    public Map<String, Object> extractJobPosting(String jobText) {
        return pythonAnalysisService.callExtractJob(jobText);
    }

    private List<String> splitToList(String value) {
        if (value == null || value.isBlank()) return List.of();
        return List.of(value.split(","));
    }

    @Transactional
    public void updateResultNote(Long resultId, String note) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));
        result.setNote(note);
        analysisResultRepository.save(result);
    }

    @Transactional
    public void updateResultStatus(Long resultId, com.atlascv.atlascvbackend.entity.CandidateStatus status) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));

        CandidateStatusLog log = new CandidateStatusLog();
        log.setResult(result);
        log.setFromStatus(result.getStatus());
        log.setToStatus(status);
        statusLogRepository.save(log);

        result.setStatus(status);
        analysisResultRepository.save(result);
    }

    public List<StageLogDto> getStageLog(Long resultId) {
        return statusLogRepository.findByResultIdOrderByChangedAtAsc(resultId)
                .stream()
                .map(l -> new StageLogDto(l.getId(), l.getFromStatus().name(), l.getToStatus().name(), l.getChangedAt()))
                .collect(Collectors.toList());
    }


    @Transactional
    public void bulkUpdateStatus(java.util.List<Long> resultIds, com.atlascv.atlascvbackend.entity.CandidateStatus status) {
        if (resultIds == null || resultIds.isEmpty()) return;
        analysisResultRepository.bulkUpdateStatus(resultIds, status);
    }

    public List<PipelineResultDto> getPipelineResults(Long userId) {
        return analysisResultRepository.findLatestRunResultsByUserId(userId)
                .stream()
                .map(this::mapToPipelineDto)
                .collect(Collectors.toList());
    }

    private PipelineResultDto mapToPipelineDto(AnalysisResult result) {
        PipelineResultDto dto = new PipelineResultDto();
        dto.setId(result.getId());
        dto.setCandidateName(result.getCandidateName());
        dto.setFileName(result.getFileName());
        dto.setFinalScore(result.getFinalScore() != null ? result.getFinalScore() : 0);
        dto.setStatus(result.getStatus() != null ? result.getStatus().name() : "BEKLEMEDE");
        dto.setMatchedHardSkills(result.getMatchedHardSkills());
        dto.setMissingHardSkills(result.getMissingHardSkills());
        dto.setMatchedSoftSkills(result.getMatchedSoftSkills());
        dto.setExperienceYears(result.getExperienceYears() != null ? result.getExperienceYears() : 0);
        dto.setSummary(result.getSummary());
        dto.setNote(result.getNote());
        dto.setAnalysisFileId(result.getAnalysisFile() != null ? result.getAnalysisFile().getId() : null);
        dto.setCandidateEmail(result.getCandidateEmail());
        AnalysisRun run = result.getAnalysisRun();
        dto.setRunId(run.getId());
        Analysis analysis = run.getAnalysis();
        dto.setAnalysisId(analysis.getId());
        dto.setAnalysisName(analysis.getAnalysisName());
        dto.setPositionName(analysis.getPositionName());
        dto.setAnalysisCreatedAt(analysis.getCreatedAt());
        dto.setInterviewDate(result.getInterviewDate());
        return dto;
    }

    @Transactional
    public void updateInterviewDate(Long resultId, java.time.LocalDateTime interviewDate) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));
        result.setInterviewDate(interviewDate);
        analysisResultRepository.save(result);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email))
                .getId();
    }

    @Transactional(readOnly = true)
    public void assertAnalysisOwner(UUID analysisId, Long userId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));
        if (!analysis.getUser().getId().equals(userId)) {
            throw new SecurityException("Bu analize erişim yetkiniz yok.");
        }
    }

    @Transactional(readOnly = true)
    public void assertRunOwner(UUID runId, Long userId) {
        AnalysisRun run = analysisRunRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Çalıştırma bulunamadı. ID: " + runId));
        if (!run.getAnalysis().getUser().getId().equals(userId)) {
            throw new SecurityException("Bu çalıştırmaya erişim yetkiniz yok.");
        }
    }

    @Transactional(readOnly = true)
    public void assertResultOwner(Long resultId, Long userId) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));
        if (!result.getAnalysisRun().getAnalysis().getUser().getId().equals(userId)) {
            throw new SecurityException("Bu sonuca erişim yetkiniz yok.");
        }
    }

    @Transactional(readOnly = true)
    public void assertFileOwner(Long fileId, Long userId) {
        AnalysisFile file = analysisFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Dosya bulunamadı. ID: " + fileId));
        if (!file.getAnalysis().getUser().getId().equals(userId)) {
            throw new SecurityException("Bu dosyaya erişim yetkiniz yok.");
        }
    }

    public ResponseEntity<byte[]> getFileContent(Long fileId) {
        AnalysisFile file = analysisFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Dosya bulunamadı. ID: " + fileId));
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getFilePath()));
            String fname = file.getOriginalFileName() != null ? file.getOriginalFileName().toLowerCase() : "";
            String contentType = fname.endsWith(".docx")
                    ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    : "application/pdf";
            String disposition = fname.endsWith(".docx")
                    ? "attachment; filename=\"" + file.getOriginalFileName() + "\""
                    : "inline; filename=\"" + file.getOriginalFileName() + "\"";
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", disposition)
                    .header("Access-Control-Expose-Headers", "Content-Disposition")
                    .body(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Dosya okunamadı: " + file.getFilePath(), e);
        }
    }
}