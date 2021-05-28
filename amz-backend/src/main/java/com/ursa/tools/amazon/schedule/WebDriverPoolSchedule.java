package com.ursa.tools.amazon.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ursa.tools.amazon.util.WebDriverPool;

@Configuration
@EnableScheduling
public class WebDriverPoolSchedule {
	
	@Scheduled(fixedRate = 60000)
	public void cleanExpired() {
		WebDriverPool pool = WebDriverPool.getInstance();
		pool.cleanExpired();
	}
}
