package com.secure.code.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "secure-code-uploads");

    public File saveFile(MultipartFile file) throws IOException {
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = tempDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toFile();
    }
}
