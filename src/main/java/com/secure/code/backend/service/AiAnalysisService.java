package com.secure.code.backend.service;

import com.secure.code.backend.model.Vulnerability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAnalysisService {

    private final ChatClient.Builder chatClientBuilder;

    public List<Vulnerability> analyzeCode(String code, String language) {
        log.info("Starting AI analysis for language: {}", language);
        ChatClient chatClient = chatClientBuilder.build();

        String prompt = String.format("""
                Analyze the following %s code for security vulnerabilities.
                Focus on OWASP Top 10 issues.
                Provide the output in the following format for each vulnerability found:

                [VULNERABILITY]
                RuleID: <Short ID>
                Description: <Short description>
                Severity: <HIGH/MEDIUM/LOW>
                Category: <OWASP Category>
                Line: <Line number (approx)>
                Explanation: <Detailed explanation>
                Fix: <Code fix suggestion>
                [END]

                Code:
                %s
                """, language, code);

        // Truncate code if too long for context window (simplistic approach)
        if (prompt.length() > 10000) {
            prompt = prompt.substring(0, 10000) + "\n... (truncated)";
        }

        try {
            log.info("Sending prompt to AI: {}", prompt);
            String response = chatClient.prompt().user(prompt).call().content();
            log.info("Raw AI Response: {}", response);
            return parseAiResponse(response);
        } catch (Exception e) {
            log.error("AI Analysis failed", e);
            return new ArrayList<>();
        }
    }

    private List<Vulnerability> parseAiResponse(String response) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        String[] lines = response.split("\n");
        Vulnerability currentVuln = null;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty())
                continue;

            // Check for new vulnerability marker (RuleID is the most reliable unique field)
            if (trimmed.contains("RuleID:")) {
                if (currentVuln != null) {
                    vulnerabilities.add(currentVuln);
                }
                currentVuln = new Vulnerability();
                currentVuln.setRuleId(extractValue(trimmed, "RuleID"));
                continue;
            }

            if (currentVuln != null) {
                if (trimmed.contains("Description:") || trimmed.contains("**Description:**")) {
                    currentVuln.setDescription(extractValue(trimmed, "Description"));
                } else if (trimmed.contains("Severity:") || trimmed.contains("**Severity:**")) {
                    currentVuln.setSeverity(extractValue(trimmed, "Severity"));
                } else if (trimmed.contains("Category:") || trimmed.contains("**Category:**")) {
                    currentVuln.setCategory(extractValue(trimmed, "Category"));
                } else if (trimmed.contains("Line:") || trimmed.contains("**Line:**")) {
                    try {
                        String lineVal = extractValue(trimmed, "Line").replaceAll("[^0-9]", "");
                        if (!lineVal.isEmpty()) {
                            currentVuln.setLineNumber(Integer.parseInt(lineVal));
                        }
                    } catch (NumberFormatException e) {
                        currentVuln.setLineNumber(0);
                    }
                } else if (trimmed.contains("Explanation:") || trimmed.contains("**Explanation:**")) {
                    currentVuln.setAiExplanation(extractValue(trimmed, "Explanation"));
                } else if (trimmed.contains("Fix:") || trimmed.contains("**Fix:**")) {
                    currentVuln.setAiFixSuggestion(extractValue(trimmed, "Fix"));
                } else {
                    // Append text to the last modified field if applicable
                    // Heuristic: if we have explanation but not fix yet, it's explanation
                    // continuation
                    if (currentVuln.getAiExplanation() != null && currentVuln.getAiFixSuggestion() == null) {
                        currentVuln.setAiExplanation(currentVuln.getAiExplanation() + " " + trimmed);
                    } else if (currentVuln.getAiFixSuggestion() != null) {
                        currentVuln.setAiFixSuggestion(currentVuln.getAiFixSuggestion() + "\n" + trimmed);
                    }
                }
            }
        }
        if (currentVuln != null) {
            vulnerabilities.add(currentVuln);
        }
        return vulnerabilities;
    }

    private String extractValue(String line, String key) {
        String cleanLine = line.replaceAll("\\*\\*", "").replaceAll("###", "").replaceAll("- ", "").trim();
        int idx = cleanLine.indexOf(key + ":");
        if (idx != -1) {
            return cleanLine.substring(idx + key.length() + 1).trim();
        }
        return "";
    }
}
