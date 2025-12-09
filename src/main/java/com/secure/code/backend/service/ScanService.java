package com.secure.code.backend.service;

import com.secure.code.backend.model.*;
import com.secure.code.backend.repository.ScanResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanService {

    private final FileStorageService fileStorageService;
    private final CodeParserService codeParserService;
    private final AiAnalysisService aiAnalysisService;
    private final SpotBugsService spotBugsService; // In real app, inject list of services
    private final ScanResultRepository scanResultRepository;

    public ScanResult performScan(MultipartFile file, User user) {
        ScanResult result = ScanResult.builder()
                .user(user)
                .scanDate(LocalDateTime.now())
                .status(ScanStatus.IN_PROGRESS)
                .repositoryUrl(file.getOriginalFilename()) // Simulating repo URL
                .build();

        result = scanResultRepository.save(result);
        if (result == null)
            throw new RuntimeException("Failed to save scan result");

        try {
            File savedFile = fileStorageService.saveFile(file);
            String language = determineLanguage(savedFile.getName());
            String codeContent = codeParserService.parseFileContent(savedFile);

            List<Vulnerability> allVulns = new ArrayList<>();

            // 1. AI Analysis
            List<Vulnerability> aiVulns = aiAnalysisService.analyzeCode(codeContent, language);
            log.info("AI found {} vulnerabilities", aiVulns.size());
            allVulns.addAll(aiVulns);

            // 2. Static Analysis (SpotBugs - Java only for now)
            if ("java".equalsIgnoreCase(language)) {
                List<Vulnerability> staticVulns = spotBugsService.analyze(savedFile);
                log.info("Static analysis found {} vulnerabilities", staticVulns.size());
                allVulns.addAll(staticVulns);
            }

            // Update entities
            for (Vulnerability v : allVulns) {
                v.setScanResult(result);
                v.setFilePath(savedFile.getName());
            }

            result.setVulnerabilities(allVulns);
            result.setStatus(ScanStatus.COMPLETED);

        } catch (Exception e) {
            log.error("Scan failed", e);
            result.setStatus(ScanStatus.FAILED);
        }

        return scanResultRepository.save(result);
    }

    private String determineLanguage(String filename) {
        if (filename.endsWith(".java"))
            return "java";
        if (filename.endsWith(".py"))
            return "python";
        if (filename.endsWith(".js"))
            return "javascript";
        return "unknown";
    }
}
