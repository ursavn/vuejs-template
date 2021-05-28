package com.ursa.tools.amazon.util;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverFactory {

	public static WebDriver createWebdriver() throws Exception {
		 return new RemoteWebDriver(new URL("http://chrome:4444/wd/hub"), DesiredCapabilities.chrome());
	}
}
