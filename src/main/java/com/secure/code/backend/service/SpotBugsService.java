package com.secure.code.backend.service;

import com.secure.code.backend.model.Vulnerability;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpotBugsService implements StaticAnalysisService {
    @Override
    public List<Vulnerability> analyze(File projectDir) {
        // Placeholder: In a real app, this would run ProcessBuilder("java", "-jar",
        // "spotbugs.jar", ...)
        // For now, we return empty list and let AI do the heavy lifting for the demo
        return new ArrayList<>();
    }

    @Override
    public boolean supportsLanguage(String language) {
        return "java".equalsIgnoreCase(language);
    }
}
