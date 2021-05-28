package com.ursa.tools.amazon.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.model.AutomationLog;
import com.ursa.tools.amazon.repository.AutomationLogRepository;

@Service
public class AutomationLogServiceImpl implements AutomationLogService {
	
	@Value("${automation.log.timeout}")
	private int logTimeout;
	
	@Autowired
	AutomationLogRepository automationLogRepository;

	@Override
	public Page<AutomationLog> findByAutomationId(Integer automationId, Pageable pageable) {
		return automationLogRepository.findByAutomationId(automationId, pageable);
	}

	@Override
	public AutomationLog save(AutomationLog log) {
		return automationLogRepository.save(log);
	}

	@Override
	@Transactional
	public long cleanLog() {
		if(logTimeout == 0) 
			logTimeout = 7;
		return automationLogRepository.deleteByCreatedDateLessThan(LocalDateTime.now().minusDays(logTimeout));
	}

}
