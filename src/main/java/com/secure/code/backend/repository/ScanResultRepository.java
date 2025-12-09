package com.secure.code.backend.repository;

import com.secure.code.backend.model.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findByUserId(Long userId);
}
