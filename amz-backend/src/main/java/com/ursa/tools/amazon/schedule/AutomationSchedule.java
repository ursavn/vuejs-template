package com.ursa.tools.amazon.schedule;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.constant.enums.AutomationLogType;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.AutomationLog;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.service.AutomationLogService;
import com.ursa.tools.amazon.service.AutomationService;

@Configuration
@EnableScheduling
public class AutomationSchedule {	

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${schedule.enable}")
	private boolean enable;
	
	@Value("${async.config.max-pool-size}")
	private int maxSize;
	
	@Autowired
	AutomationAsyncTask automationAsyncTask;

	@Autowired
	AmazonService amazonService;

	@Autowired
	AmazonAccountService amazonAccountService;

	@Autowired
	AutomationService automationService;

	@Autowired
	AutomationLogService automationLogService;

	@Scheduled(fixedDelayString = "${schedule.fixedDelay}", initialDelay = 10000)
	public void task() {
		if(!enable) return;
		
		logger.info("--- Start schedule ---");
		long beginTime = System.currentTimeMillis();	
		automationLogService.save(new AutomationLog(AutomationLogType.START, "Start schedule", null));
		
		int count = this.taskRun(1);
		
		String endLog = "End: Crawl " + count + " product on " + ((System.currentTimeMillis() - beginTime) / 1000) + "s";
		automationLogService.save(new AutomationLog(AutomationLogType.END, endLog, null));
		logger.info(endLog);		
	}

	private int taskRun(int count) {
		Page<AmazonAccount> pageAccount = amazonAccountService.findByStatus(AmazonAccountStatus.ACTIVE, PageRequest.of(count - 1, maxSize));
		List<AmazonAccount> amazonAccounts = pageAccount.getContent();
		int success = 0;
		if (amazonAccounts.size() > 0) {				
			List<CompletableFuture<AutomationAsyncTaskResult>> tasks = amazonAccounts.stream()
					.map(account -> automationAsyncTask.execute(account))
					.collect(Collectors.toList());
			
			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
						
			for(CompletableFuture<AutomationAsyncTaskResult> task : tasks) {
				try {
					AutomationAsyncTaskResult result = task.get();
					success += result.getSuccess();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			if(amazonAccounts.size() == maxSize) {
				success = success + this.taskRun(count + 1);
			}
		}	
		return success;
	}
}
