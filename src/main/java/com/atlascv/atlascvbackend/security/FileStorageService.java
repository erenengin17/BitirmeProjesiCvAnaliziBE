package com.atlascv.atlascvbackend.security;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String copyFile(UUID targetAnalysisId, Path sourcePath, String originalFileName) {
        try {
            Path analysisFolder = root.resolve("analysis-" + targetAnalysisId);
            if (!Files.exists(analysisFolder)) {
                Files.createDirectories(analysisFolder);
            }
            String storedName = UUID.randomUUID() + "_" + originalFileName;
            Path target = analysisFolder.resolve(storedName);
            Files.copy(sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Dosya kopyalanamadı", e);
        }
    }

    public String storeFile(UUID analysisId, MultipartFile file) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Path analysisFolder = root.resolve("analysis-" + analysisId);

            if (!Files.exists(analysisFolder)) {
                Files.createDirectories(analysisFolder);
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isBlank()) {
                originalName = "unknown.pdf";
            }
            // Windows'ta geçersiz karakter temizleme
            String safeName = originalName.replaceAll("[<>:\"/\\\\|?*]", "_");
            // MAX_PATH limiti için kırpma (260 - base path ~120 - UUID prefix 37 = 100 karakter)
            if (safeName.length() > 100) {
                String ext = safeName.contains(".") ? safeName.substring(safeName.lastIndexOf('.')) : "";
                safeName = safeName.substring(0, 95 - ext.length()) + ext;
            }
            String storedName = UUID.randomUUID() + "_" + safeName;

            Path target = analysisFolder.resolve(storedName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi [" + file.getOriginalFilename() + "]: " + e.getMessage(), e);
        }
    }
}