package com.secure.code.backend.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CodeParserService {

    public List<String> extractMethods(File file) {
        List<String> methods = new ArrayList<>();
        try {
            if (file.getName().endsWith(".java")) {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(MethodDeclaration.class).forEach(method -> {
                    methods.add(method.toString());
                });
            } else {
                // For other languages, just read file content for now (simulated parsing)
                methods.add(Files.readString(file.toPath()));
            }
        } catch (IOException e) {
            log.error("Error parsing file: {}", file.getName(), e);
        }
        return methods;
    }

    public String parseFileContent(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            log.error("Failed to read file", e);
            return "";
        }
    }
}
