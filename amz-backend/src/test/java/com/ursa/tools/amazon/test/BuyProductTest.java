package com.ursa.tools.amazon.test;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.OrderData;
import com.ursa.tools.amazon.model.page.data.ProductData;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.service.AmazonService;
import com.ursa.tools.amazon.util.WebDriverFactory;

@SpringBootTest
public class BuyProductTest {
	
	@Autowired
	AmazonService amazonService;
	
	@Autowired
	AmazonAccountService amazonAccountService;
	
	//@Test
	public void buyProduct() {
		try {
			String asin = "B07VQK5861";
			long beginTime = System.currentTimeMillis() / 1000;
			WebDriver driver = WebDriverFactory.createWebdriver();
			AmazonAccount account = amazonAccountService.getRandomAccount().get();			
			CrawlProductData crawlProductData = amazonService.crawlProduct(driver, account, asin);
			ProductData productData = crawlProductData.getData();
			if (productData != null) {
				int buyQuantity = 1;
				int sellerIndex = productData.getSellers().get(0).getIndex();
				OrderData orderData = amazonService.buyProduct(driver, account, buyQuantity, sellerIndex);
				System.out.println("Succcess: " + orderData.isSuccess());
				System.out.println("Message: " + orderData.getMessage());
			}
			System.out.println("Time: " + (System.currentTimeMillis() / 1000 - beginTime) + "(s)");
			Thread.sleep(1000000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
