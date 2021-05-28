package com.ursa.tools.amazon.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.ursa.tools.amazon.model.AutomationLog;

@Repository
public interface AutomationLogRepository extends JpaRepository<AutomationLog, Integer> {
    Page<AutomationLog> findByAutomationId(Integer automationId, Pageable pageable);    
    
    @Modifying
    long deleteByCreatedDateLessThan(LocalDateTime date);
} 
