package com.ursa.tools.amazon.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebDriverPool {
	private static WebDriverPool instance;
	private Map<String, WebDriverItem> drivers = new HashMap<String, WebDriverItem>();
	
	public static WebDriverPool getInstance() {
		if(instance == null) {
			instance = new WebDriverPool();
		}
		return instance;
	}
	
	public WebDriverItem getDriver(String email) {
		return this.drivers.get(email);
	}
	
	public void putDriver(String email, WebDriverItem item) {
		this.drivers.put(email, item);
	}
	
	public void cleanDriver(String email) {
		this.drivers.remove(email);
	}
	
	public void cleanExpired() {
		if(this.drivers.size() > 0) {
			for(String key : this.drivers.keySet()) {
				WebDriverItem item = this.drivers.get(key);
				if(item.getExpiredDate().compareTo(new Date()) < 0) {
					item.getDriver().quit();
					this.drivers.remove(key);
				}
			}
		}
	}
}
