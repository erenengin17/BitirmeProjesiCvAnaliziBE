package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.PythonAnalysisResponse;
import com.atlascv.atlascvbackend.entity.AnalysisFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class PythonAnalysisService {

    private final WebClient webClient;

    public PythonAnalysisService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8001")
                .build();
    }

    public PythonAnalysisResponse runAnalysis(
            String description,
            String mustHaveSkills,
            String softSkillRequirements,
            String educationRequirements,
            Double minExperienceYears,
            Boolean requireProjectOrCertificate,
            Boolean useSemanticSimilarity,
            List<AnalysisFile> files
    ) {
        try {
            System.out.println("PYTHON SERVICE METHOD BAŞLADI");

            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            bodyBuilder.part("description", description == null ? "" : description);
            bodyBuilder.part("must_have_skills", mustHaveSkills == null ? "" : mustHaveSkills);
            bodyBuilder.part("soft_skill_requirements", softSkillRequirements == null ? "" : softSkillRequirements);
            bodyBuilder.part("education_requirements", educationRequirements == null ? "" : educationRequirements);
            bodyBuilder.part("min_experience_years", String.valueOf(minExperienceYears == null ? 0 : minExperienceYears));
            bodyBuilder.part(
                    "require_project_or_certificate",
                    String.valueOf(requireProjectOrCertificate != null && requireProjectOrCertificate)
            );
            bodyBuilder.part(
                    "use_semantic_similarity",
                    String.valueOf(useSemanticSimilarity == null || useSemanticSimilarity)
            );

            for (AnalysisFile file : files) {
                System.out.println("PYTHON'A GÖNDERİLEN DOSYA: " + file.getFilePath());

                Path path = Path.of(file.getFilePath());
                byte[] fileBytes = Files.readAllBytes(path);

                ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFileName();
                    }
                };

                String origName = file.getOriginalFileName() != null ? file.getOriginalFileName().toLowerCase() : "";
                MediaType fileContentType = origName.endsWith(".docx")
                        ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        : MediaType.APPLICATION_PDF;
                bodyBuilder.part("files", resource)
                        .filename(file.getOriginalFileName())
                        .contentType(fileContentType);
            }

            PythonAnalysisResponse response = webClient.post()
                    .uri("/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(PythonAnalysisResponse.class)
                    .block(Duration.ofSeconds(600));

            System.out.println("PYTHON SERVICE RESPONSE GELDİ");
            return response;

        } catch (WebClientResponseException e) {
            System.out.println("PYTHON HTTP STATUS: " + e.getStatusCode());
            System.out.println("PYTHON RESPONSE BODY: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw new RuntimeException("Python servisi HTTP hatası döndü: " + e.getStatusCode(), e);

        } catch (Exception e) {
            System.out.println("PYTHON SERVICE GENEL HATA:");
            e.printStackTrace();
            throw new RuntimeException("Python servisine giderken hata oluştu.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public String callExplain(Map<String, Object> body) {
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/explain")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(60));
            return response != null ? (String) response.get("explanation") : "Açıklama oluşturulamadı.";
        } catch (Exception e) {
            System.out.println("[Explain] Python hatası: " + e.getMessage());
            return "Puan gerekçesi şu an oluşturulamıyor.";
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> callExtractJob(String jobText) {
        try {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("job_text", jobText);
            Map<String, Object> response = webClient.post()
                    .uri("/extract-job")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(30));
            return response != null ? response : new java.util.HashMap<>();
        } catch (Exception e) {
            System.out.println("[ExtractJob] Python hatası: " + e.getMessage());
            throw new RuntimeException("İş ilanı analizi şu an kullanılamıyor.");
        }
    }
}