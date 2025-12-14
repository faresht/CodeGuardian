package com.secure.code.backend.dto;

import com.secure.code.backend.model.ScanStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ScanResultResponse {
    private Long id;
    private String username;
    private String repositoryUrl;
    private ScanStatus status;
    private LocalDateTime scanDate;
    private List<VulnerabilityResponse> vulnerabilities;
}
