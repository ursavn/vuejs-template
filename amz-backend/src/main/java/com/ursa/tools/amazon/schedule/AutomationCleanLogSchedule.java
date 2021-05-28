package com.ursa.tools.amazon.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ursa.tools.amazon.service.AutomationLogService;

@Configuration
@EnableScheduling
public class AutomationCleanLogSchedule {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	AutomationLogService automationLogService;

	/**
	 * At 00:01 every day
	 */
	@Scheduled(cron = "0 1 0 * * ?")
	public void cleanLog() {
		logger.info("Begin task clean automation log");
		
		long number = automationLogService.cleanLog();
		
		logger.info("Clean {} automation log", number);
	}
}
