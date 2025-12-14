package com.secure.code.backend.controller;

import com.secure.code.backend.dto.ScanResultResponse;
import com.secure.code.backend.dto.VulnerabilityResponse;
import com.secure.code.backend.model.ScanResult;
import com.secure.code.backend.model.User;
import com.secure.code.backend.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ScanResultResponse> startScan(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        ScanResult result = scanService.performScan(file, user);

        return ResponseEntity.ok(ScanResultResponse.builder()
                .id(result.getId())
                .username(result.getUser().getUsername())
                .repositoryUrl(result.getRepositoryUrl())
                .status(result.getStatus())
                .scanDate(result.getScanDate())
                .vulnerabilities(result.getVulnerabilities().stream()
                        .map(v -> VulnerabilityResponse.builder()
                                .ruleId(v.getRuleId())
                                .description(v.getDescription())
                                .severity(v.getSeverity())
                                .category(v.getCategory())
                                .filePath(v.getFilePath())
                                .lineNumber(v.getLineNumber())
                                .aiExplanation(v.getAiExplanation())
                                .aiFixSuggestion(v.getAiFixSuggestion())
                                .build())
                        .toList())
                .build());
    }
}
