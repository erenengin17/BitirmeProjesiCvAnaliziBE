package com.atlascv.atlascvbackend.security;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String storeFile(Long analysisId, MultipartFile file) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Path analysisFolder = root.resolve("analysis-" + analysisId);

            if (!Files.exists(analysisFolder)) {
                Files.createDirectories(analysisFolder);
            }

            String originalName = file.getOriginalFilename();
            String storedName = UUID.randomUUID() + "_" + originalName;

            Path target = analysisFolder.resolve(storedName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi", e);
        }
    }
}