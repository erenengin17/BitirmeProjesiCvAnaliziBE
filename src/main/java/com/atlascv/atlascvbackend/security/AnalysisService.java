package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.AnalysisResponse;
import com.atlascv.atlascvbackend.dto.PythonAnalysisResponse;
import com.atlascv.atlascvbackend.dto.PythonCandidateResult;
import com.atlascv.atlascvbackend.dto.RunAnalysisRequest;
import com.atlascv.atlascvbackend.entity.Analysis;
import com.atlascv.atlascvbackend.entity.AnalysisFile;
import com.atlascv.atlascvbackend.entity.AnalysisResult;
import com.atlascv.atlascvbackend.entity.AnalysisRun;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.AnalysisFileRepository;
import com.atlascv.atlascvbackend.repository.AnalysisRepository;
import com.atlascv.atlascvbackend.repository.AnalysisResultRepository;
import com.atlascv.atlascvbackend.repository.AnalysisRunRepository;
import com.atlascv.atlascvbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final AnalysisFileRepository analysisFileRepository;
    private final AnalysisRunRepository analysisRunRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final FileStorageService fileStorageService;
    private final PythonAnalysisService pythonAnalysisService;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            UserRepository userRepository,
            AnalysisFileRepository analysisFileRepository,
            AnalysisRunRepository analysisRunRepository,
            AnalysisResultRepository analysisResultRepository,
            FileStorageService fileStorageService,
            PythonAnalysisService pythonAnalysisService
    ) {
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
        this.analysisFileRepository = analysisFileRepository;
        this.analysisRunRepository = analysisRunRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.fileStorageService = fileStorageService;
        this.pythonAnalysisService = pythonAnalysisService;
    }

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
        analysis.setCvCount(files != null ? files.length : 0);
        analysis.setStatus("Bekliyor");
        analysis.setUser(user);

        Analysis savedAnalysis = analysisRepository.save(analysis);

        if (files != null) {
            for (MultipartFile file : files) {
                String savedPath = fileStorageService.storeFile(savedAnalysis.getId(), file);

                AnalysisFile analysisFile = new AnalysisFile();
                analysisFile.setAnalysis(savedAnalysis);
                analysisFile.setOriginalFileName(file.getOriginalFilename());
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

    public AnalysisResponse getAnalysisById(Long analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));

        return mapToResponse(analysis);
    }

    public Long runAnalysis(Long analysisId, RunAnalysisRequest request) {
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

            analysisResultRepository.save(result);
            System.out.println("Result DB'ye yazıldı: " + item.getCandidateName());
        }

        analysis.setStatus("Tamamlandi");
        analysisRepository.save(analysis);

        System.out.println("RUN başarıyla tamamlandı. runId = " + savedRun.getId());
        return savedRun.getId();
    }

    public List<AnalysisResult> getRunResults(Long runId) {
        return analysisResultRepository.findByAnalysisRunIdOrderByFinalScoreDesc(runId);
    }

    public List<AnalysisFile> getAnalysisFiles(Long analysisId) {
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
    public void deleteAnalysis(Long analysisId) {
        List<AnalysisRun> runs = analysisRunRepository.findByAnalysisIdOrderByCreatedAtDesc(analysisId);
        for (AnalysisRun run : runs) {
            List<AnalysisResult> results = analysisResultRepository.findByAnalysisRunIdOrderByFinalScoreDesc(run.getId());
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

    public AnalysisResponse updateAnalysis(Long analysisId, String analysisName, String positionName, String description) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analiz bulunamadı. ID: " + analysisId));
        if (analysisName != null && !analysisName.isBlank()) analysis.setAnalysisName(analysisName);
        if (positionName != null && !positionName.isBlank()) analysis.setPositionName(positionName);
        analysis.setDescription(description);
        return mapToResponse(analysisRepository.save(analysis));
    }

    public List<AnalysisRun> getAnalysisRuns(Long analysisId) {
        return analysisRunRepository.findByAnalysisIdOrderByCreatedAtDesc(analysisId);
    }

    public AnalysisRun getLastRun(Long analysisId) {
        return analysisRunRepository
                .findTopByAnalysisIdOrderByCreatedAtDesc(analysisId)
                .orElse(null);
    }

    public void updateResultNote(Long resultId, String note) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Sonuç bulunamadı. ID: " + resultId));
        result.setNote(note);
        analysisResultRepository.save(result);
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