package com.ursa.tools.amazon.model.page;

import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ursa.tools.amazon.constant.enums.AmazonAccountStatus;
import com.ursa.tools.amazon.model.AmazonAccount;
import com.ursa.tools.amazon.service.AmazonAccountService;
import com.ursa.tools.amazon.util.WebDriverItem;
import com.ursa.tools.amazon.util.WebDriverPool;

@Component
public class LoginVerifyPage {

	@Autowired
	AmazonAccountService amazonAccountService;
	
	@Async("asyncExecutor")
	public void verifyLogin(LoginPage loginPage) {
		try {
			CompletableFuture<String> cookies = loginPage.verifyLogin();
			CompletableFuture.allOf(cookies).join();
			WebDriver driver = loginPage.getWebDriver();
			
			if(cookies.get() != null) {
				Optional<AmazonAccount> optionalAccount = amazonAccountService.findByEmail(loginPage.getEmail());
				AmazonAccount account = optionalAccount.get();
				account.setStatus(AmazonAccountStatus.ACTIVE);
				account.setCookies(cookies.get());
				amazonAccountService.save(account);
				driver.quit();
			} else if(driver.findElements(By.id("captchacharacters")).size() > 0) {
				//Captcha
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 3);
				WebDriverItem item = new WebDriverItem(driver, loginPage.getEmail(), loginPage.getPassword(), calendar.getTime());
				WebDriverPool pool = WebDriverPool.getInstance();
				pool.putDriver(loginPage.getEmail(), item);
				
				String link = driver.findElement(By.tagName("img")).getAttribute("src");
				Optional<AmazonAccount> optionalAccount = amazonAccountService.findByEmail(loginPage.getEmail());
				AmazonAccount account = optionalAccount.get();
				account.setStatus(AmazonAccountStatus.CAPTCHA);
				account.setStatusNote(link);
				amazonAccountService.save(account);
			} else {
				driver.quit();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
