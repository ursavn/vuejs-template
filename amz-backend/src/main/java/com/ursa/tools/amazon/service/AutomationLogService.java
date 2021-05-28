package com.ursa.tools.amazon.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ursa.tools.amazon.model.AutomationLog;

public interface AutomationLogService {
	Page<AutomationLog> findByAutomationId(Integer automationId, Pageable pageable);
	AutomationLog save(AutomationLog log);
	long cleanLog();
}
