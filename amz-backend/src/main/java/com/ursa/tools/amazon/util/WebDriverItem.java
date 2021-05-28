package com.ursa.tools.amazon.util;

import java.util.Date;

import org.openqa.selenium.WebDriver;

public class WebDriverItem {
	private WebDriver driver;
	private String email;
	private String password;	
    private Date expiredDate;
    
    public WebDriverItem(WebDriver driver, String email, String password, Date expireDate) {
    	this.driver = driver;
    	this.email = email;
    	this.password = password;
    	this.expiredDate = expireDate;
    }

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
