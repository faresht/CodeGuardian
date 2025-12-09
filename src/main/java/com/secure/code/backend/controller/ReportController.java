package com.secure.code.backend.controller;

import com.secure.code.backend.model.ScanResult;
import com.secure.code.backend.model.User;
import com.secure.code.backend.repository.ScanResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ScanResultRepository scanResultRepository;

    @GetMapping
    public ResponseEntity<List<ScanResult>> getMyScans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(scanResultRepository.findByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanResult> getScanDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return scanResultRepository.findById(id)
                .filter(scan -> scan.getUser() != null && scan.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
