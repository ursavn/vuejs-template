package com.ursa.tools.amazon.service;

import javax.transaction.Transactional;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.constant.enums.LoginAmazonStatus;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.model.page.LoginPage;
import com.ursa.tools.amazon.model.page.LoginVerifyPage;
import com.ursa.tools.amazon.model.page.ProductPage;
import com.ursa.tools.amazon.model.page.data.CrawlProductData;
import com.ursa.tools.amazon.model.page.data.LoginData;
import com.ursa.tools.amazon.model.page.data.OrderData;
import com.ursa.tools.amazon.repository.ProductRepository;
import com.ursa.tools.amazon.util.WebDriverFactory;
import com.ursa.tools.amazon.util.WebDriverItem;

@Service
public class AmazonServiceImpl implements AmazonService {
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	AmazonAccountService amazonAccountService;
	
	@Autowired
	LoginVerifyPage loginVerifyPage;

	@Override
	public LoginData loginAmazon(String email, String password) throws Exception {
		WebDriver driver = WebDriverFactory.createWebdriver();
		LoginPage loginPage = new LoginPage(driver);
		loginPage.view();
		LoginData data = loginPage.login(email, password);
		
		if(data.getStatus().equals(LoginAmazonStatus.VERIFY)) {
			loginVerifyPage.verifyLogin(loginPage);
		}
		return data;
	}
	
	@Override
	public WebDriver initWebDriver(AmazonAccount account) throws Exception {
		WebDriver driver = WebDriverFactory.createWebdriver();
		LoginPage loginPage = new LoginPage(driver);
		loginPage.view();
		if(account != null) {
			loginPage.changeCookies(account.getCookies());
		}
		return driver;
	}

	@Override
	@Transactional
	public CrawlProductData crawlProduct(WebDriver driver, AmazonAccount account, String asin) {
		ProductPage productPage = new ProductPage(driver, null);
		productPage.viewProduct(asin);
		if(productPage.hasCaptcha() && account != null) {
			//Block account
			account.setStatus(AmazonAccountStatus.CAPTCHA);
			amazonAccountService.save(account);
			return new CrawlProductData(null, "CAPTCHA");
		}		
		return new CrawlProductData(productPage.getProduct(), "");
	}

	@Override
	public LoginData sendOtp(WebDriverItem item, String code) throws Exception {		
		if(item != null) {
			LoginPage loginPage = new LoginPage(item.getDriver());
			LoginData data = loginPage.sendOtp(item.getEmail(), item.getPassword(), code);
			if(data.getStatus().equals(LoginAmazonStatus.VERIFY)) {
				loginVerifyPage.verifyLogin(loginPage);
			}
			return data;
		}
		return new LoginData(LoginAmazonStatus.ERROR, "Not found driver");
	}
	
	@Override
	public LoginData sendCaptcha(WebDriverItem item, String code) throws Exception {		
		if(item != null) {
			LoginPage loginPage = new LoginPage(item.getDriver());
			LoginData data = loginPage.sendCaptcha(item.getEmail(), item.getPassword(), code);
			if(data.getStatus().equals(LoginAmazonStatus.VERIFY)) {
				loginVerifyPage.verifyLogin(loginPage);
			}
			return data;
		}
		return new LoginData(LoginAmazonStatus.ERROR, "Not found driver");
	}
	
	@Override
	public OrderData buyProduct(WebDriver driver, AmazonAccount account, int quantity, int sellerIndex) {			
		ProductPage productPage = new ProductPage(driver, account);				
		return productPage.buy(quantity, sellerIndex);
	}	

}
