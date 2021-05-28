package com.ursa.tools.amazon.service;

import org.openqa.selenium.WebDriver;

import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.LoginData;
import com.ursa.tools.amazon.model.page.data.OrderData;
import com.ursa.tools.amazon.util.WebDriverItem;

public interface AmazonService {
	public LoginData loginAmazon(String email, String password) throws Exception;
	public LoginData sendOtp(WebDriverItem item, String code) throws Exception;
	public LoginData sendCaptcha(WebDriverItem item, String code) throws Exception;
	public WebDriver initWebDriver(AmazonAccount account) throws Exception;
	public CrawlProductData crawlProduct(WebDriver driver, AmazonAccount account, String asin);
	public OrderData buyProduct(WebDriver driver, AmazonAccount account, int quantity, int sellerIndex);
}
