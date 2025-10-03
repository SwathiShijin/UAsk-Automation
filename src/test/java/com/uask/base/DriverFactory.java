package com.uask.base;

import java.util.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

	/**
	 * To creates WebDriver with desktop or mobile viewport 
	 * 
	 * @param device "desktop" or "mobile" 
	 * @return WebDriver instance
	 */
	public static WebDriver createDriver(String deviceName) {
		ChromeOptions options = new ChromeOptions();
		WebDriver driver;

		if (deviceName.equalsIgnoreCase("desktop")) {
			driver = new ChromeDriver(options);
			driver.manage().window().maximize();
		} else if ("iPhone14Pro".equalsIgnoreCase(deviceName)) {
			Map<String, Object> deviceMetrics = new HashMap<>();
			deviceMetrics.put("width", 430);
			deviceMetrics.put("height", 932);
			deviceMetrics.put("pixelRatio", 3.0);

			Map<String, Object> mobileEmulation = new HashMap<>();
			mobileEmulation.put("deviceMetrics", deviceMetrics);
			mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) "
					+ "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1");

			options.setExperimentalOption("mobileEmulation", mobileEmulation);
			driver = new ChromeDriver(options);
		} else {
            throw new IllegalArgumentException("Unknown device: " + deviceName);
        }
        return driver;
    }
}
