package com.ursa.tools.amazon.model.page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ursa.tools.amazon.constant.WebPage;
import com.ursa.tools.amazon.constant.enums.LoginAmazonStatus;
import com.ursa.tools.amazon.model.page.data.LoginData;
import com.ursa.tools.amazon.util.WebDriverItem;
import com.ursa.tools.amazon.util.WebDriverPool;

public class LoginPage {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@FindBy(id = "ap_email")
	private WebElement inputEmail;
	
	@FindBy(id = "ap_password")
	private WebElement inputPassword;
	
	private WebDriver driver;    
	private String email;
	private String password;
	
	public LoginPage(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(this.driver, this);
    }
	
	public void view() {
		this.driver.get(WebPage.DOMAIN);
	}
	
	public LoginData login(String email, String password) {
		if(!StringUtils.hasLength(email) || !StringUtils.hasLength(password))
			return new LoginData(LoginAmazonStatus.ERROR, "Email or password is empty");		
		try {
			this.email = email;
			this.password = password;
			//Click signin
			WebElement btnSignIn = this.driver.findElement(By.id("nav-link-accountList"));
			btnSignIn.click();	
			
			//Enter email
			this.enterEmail(email);
			String error = getError();
			if(error != null) {
				logger.info("Email " + email + " login error: " + error);
				this.driver.quit();
				return new LoginData(LoginAmazonStatus.ERROR, "email_invalid");
			}
            
			//OTP
			this.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if(this.driver.findElements(By.id("continue")).size() > 0) {
				this.driver.findElement(By.id("continue")).click();
				
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 3);
				WebDriverItem item = new WebDriverItem(driver, email, password, calendar.getTime());
				WebDriverPool pool = WebDriverPool.getInstance();
				pool.putDriver(email, item);
				return new LoginData(LoginAmazonStatus.OTP, null);
			}

			//Enter password
			this.enterPassword(password);
			error = getError();
			if(error != null) {
				logger.info("Email " + email + " login error: " + error);
				this.driver.quit();
				return new LoginData(LoginAmazonStatus.ERROR, "password_invalid");
			}      
			
			//Captcha 1
			if(this.driver.findElements(By.id("auth-captcha-image-container")).size() > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 3);
				WebDriverItem item = new WebDriverItem(driver, email, password, calendar.getTime());
				WebDriverPool pool = WebDriverPool.getInstance();
				pool.putDriver(email, item);
				
				String link = this.driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
				return new LoginData(LoginAmazonStatus.CAPTCHA, link);
			}
			//Captcha 2
			if(this.driver.findElements(By.id("captchacharacters")).size() > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 3);
				WebDriverItem item = new WebDriverItem(driver, email, password, calendar.getTime());
				WebDriverPool pool = WebDriverPool.getInstance();
				pool.putDriver(email, item);
				
				String link = this.driver.findElement(By.tagName("img")).getAttribute("src");
				return new LoginData(LoginAmazonStatus.CAPTCHA, link);
			}
			
			 //Check need verify email
			if(this.driver.findElements(By.id("resend-approval-link")).size() > 0) {
				return new LoginData(LoginAmazonStatus.VERIFY, null);
			}
			//Result cookie			
			List<Cookie> cookies = new ArrayList<>(this.driver.manage().getCookies()); 
			ObjectMapper mapper = new ObjectMapper();
			this.driver.quit();
			return new LoginData(LoginAmazonStatus.SUCCESS, mapper.writeValueAsString(cookies));
		}
		catch (Exception e) {
			e.printStackTrace();		
			this.driver.quit();
			return new LoginData(LoginAmazonStatus.ERROR, "cant_login_amazon");
		}
	}
	
	public void enterEmail(String email) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement btnContinue = wait.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
		inputEmail.sendKeys(email);
		this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		btnContinue.click();
	}
	
	public void enterPassword(String password) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement btnSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.id("signInSubmit")));
		inputPassword.sendKeys(password);
		this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		btnSubmit.click();
	}
	
	public LoginData sendOtp(String email, String password, String code) {
		this.email = email;
		if(this.driver.findElements(By.name("code")).size() > 0) {
			try {
				this.driver.findElement(By.name("code")).sendKeys(code);
				this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				this.driver.findElement(By.xpath("//input[@type='submit']")).click();
				
				//Enter password
				if(this.driver.findElements(By.id("ap_password")).size() > 0) {
					this.enterPassword(password);
					String error = getError();
					if(error != null) {
						logger.info("Email " + email + " login error: " + error);
						this.driver.quit();
						return new LoginData(LoginAmazonStatus.ERROR, "password_invalid");
					}     
				}
				
				if(this.driver.findElements(By.name("cvfDcqAction")).size() > 0) {
					return new LoginData(LoginAmazonStatus.CHANGE_PASSWORD, null);
				}
				
				//Check need captcha
				if(this.driver.findElements(By.id("auth-captcha-image-container")).size() > 0) {
					String link = this.driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
					return new LoginData(LoginAmazonStatus.CAPTCHA, link);
				}
				 //Check need verify email
				if(this.driver.findElements(By.id("resend-approval-link")).size() > 0) {
					return new LoginData(LoginAmazonStatus.VERIFY, null);
				}
				//Result cookie			
				List<Cookie> cookies = new ArrayList<>(this.driver.manage().getCookies()); 
				ObjectMapper mapper = new ObjectMapper();
				this.driver.quit();
				return new LoginData(LoginAmazonStatus.SUCCESS, mapper.writeValueAsString(cookies));
			}
			catch (Exception e) {
				e.printStackTrace();
				this.driver.quit();
			}
		}
		return new LoginData(LoginAmazonStatus.ERROR, "cant_login_amazon");
	}
	
	public LoginData sendCaptcha(String email, String password, String code) {
		try {			
			this.email = email;
			
			//Captcha 1
			if(this.driver.findElements(By.id("auth-captcha-image-container")).size() > 0) {
				inputPassword.sendKeys(password);
				this.driver.findElement(By.id("auth-captcha-guess")).sendKeys(code);			
				this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);			
				this.driver.findElement(By.id("signInSubmit")).click();
				this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				
				String error = getError();
				if(error != null) {
					this.enterPassword(password); 
					error = getError();
					if(error != null) {
						logger.info("Email " + email + " login error: " + error);
						this.driver.quit();
						return new LoginData(LoginAmazonStatus.ERROR, "password_invalid");
					}
				}
			}
			//Captcha 2
			if(this.driver.findElements(By.id("captchacharacters")).size() > 0) {
			    this.driver.findElement(By.id("captchacharacters")).sendKeys(code);
			    this.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);			
				this.driver.findElement(By.xpath("//button[@type='submit']")).click();
			}
			
			if(this.driver.findElements(By.name("cvfDcqAction")).size() > 0) {
				return new LoginData(LoginAmazonStatus.CHANGE_PASSWORD, null);
			}
			
			//OTP
			this.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if(this.driver.findElements(By.id("continue")).size() > 0) {
				this.driver.findElement(By.id("continue")).click();
				
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MINUTE, 3);
				WebDriverItem item = new WebDriverItem(driver, email, password, calendar.getTime());
				WebDriverPool pool = WebDriverPool.getInstance();
				pool.putDriver(email, item);
				return new LoginData(LoginAmazonStatus.OTP, null);
			}

			//Check captcha 1
			if(this.driver.findElements(By.id("auth-captcha-image-container")).size() > 0) {
				String link = this.driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
				return new LoginData(LoginAmazonStatus.CAPTCHA, link);
			}

			 //Check need verify email
			if(this.driver.findElements(By.id("resend-approval-link")).size() > 0) {
				return new LoginData(LoginAmazonStatus.VERIFY, null);
			}
			
			//Check captcha 2
			if(this.driver.findElements(By.id("captchacharacters")).size() > 0) {
				String link = this.driver.findElement(By.tagName("img")).getAttribute("src");
				return new LoginData(LoginAmazonStatus.CAPTCHA, link);
			}
			
			//Result cookie			
			List<Cookie> cookies = new ArrayList<>(this.driver.manage().getCookies());			
			ObjectMapper mapper = new ObjectMapper();
			this.driver.quit();
			return new LoginData(LoginAmazonStatus.SUCCESS, mapper.writeValueAsString(cookies));
		}
		catch (Exception e) {
			e.printStackTrace();
			this.driver.quit();
			return new LoginData(LoginAmazonStatus.ERROR, "can_not_login_amazon");
		}
	}
	
	@Async("asyncExecutor")
	public CompletableFuture<String> verifyLogin() {
		try {
			WebDriverWait waitSuccess = new WebDriverWait(driver, 60);
			waitSuccess.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList")),
				ExpectedConditions.visibilityOfElementLocated(By.id("captchacharacters"))
			));
			if(this.driver.findElements(By.id("nav-link-accountList")).size() > 0) {			
				List<Cookie> cookies = new ArrayList<>(this.driver.manage().getCookies()); 
				ObjectMapper mapper = new ObjectMapper();
				return CompletableFuture.completedFuture(mapper.writeValueAsString(cookies));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
	
	public boolean isLogin() {
		List<WebElement> signIns = this.driver.findElements(By.id("nav-al-signin"));
		return signIns.size() == 0;
	}
	
	public void changeCookies(String cookieJson) {
		try {
			if(cookieJson != null) {				
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String, Object>> cookies = 
						mapper.readValue(cookieJson, new TypeReference<List<Map<String, Object>>>() {});
				
				this.driver.manage().deleteAllCookies();
				for(Map<String, Object> item : cookies) {
					this.driver.manage().addCookie(new Cookie(
							(String) item.get("name"), 
							(String) item.get("value"), 
							(String) item.get("domain"), 
							(String) item.get("path"), 
							(item.get("expiry") == null ? null: new Date((long) item.get("expiry"))), 
							(boolean) item.get("secure"), 
							(boolean) item.get("httpOnly")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getError() {
		try {
			WebElement errorBox = this.driver.findElement(By.id("auth-error-message-box"));
			return errorBox.findElement(By.className("a-alert-content")).getText();
		} catch (Exception e) {
			return null;
		}
	}
	
	public WebDriver getWebDriver() {
		return this.driver;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPassword() {
		return this.password;
	}
}
