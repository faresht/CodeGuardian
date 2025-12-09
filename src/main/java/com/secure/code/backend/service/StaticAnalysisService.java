package com.secure.code.backend.service;

import com.secure.code.backend.model.Vulnerability;
import java.io.File;
import java.util.List;

public interface StaticAnalysisService {
    List<Vulnerability> analyze(File projectDir);

    boolean supportsLanguage(String language);
}
