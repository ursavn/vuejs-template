package com.ursa.tools.amazon.test.util;

import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.util.WebDriverFactory;

@Component
public class CrawlProductAsyncTask {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AmazonService amazonService;
	
	@Autowired
	AmazonAccountService amazonAccountService;
	
	@Async("asyncExecutor")
	public CompletableFuture<ProductData> execute(String asin) {	
		logger.debug("Crawl " + asin);
		long beginTime = System.currentTimeMillis();
		WebDriver driver;
		ProductData productData = null;
		try {
			AmazonAccount account = amazonAccountService.getRandomAccount().get();			
			driver = WebDriverFactory.createWebdriver();
			CrawlProductData crawlProductData = amazonService.crawlProduct(driver, account, asin);
			productData = crawlProductData.getData();
			driver.quit();		
		} catch (Exception e) {
			e.printStackTrace();			
		}
		if(productData == null) {
			productData = new ProductData();
			productData.setAsin(asin);
		}
		logger.debug("Crawl " + asin + " on {} s", (System.currentTimeMillis() - beginTime) / 1000);
		return CompletableFuture.completedFuture(productData);
	}
}
