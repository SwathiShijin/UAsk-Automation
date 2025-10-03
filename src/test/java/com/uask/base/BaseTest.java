package com.uask.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import utils.ConfigReader;
import utils.Log;

public class BaseTest {
	
	protected WebDriver driver;
    protected static ExtentReports extent;
    protected static ExtentTest test;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }

    // ----------------- Class Setup -----------------
    
    /**
     * To start a new driver session
     * 
     * @param method
     * @param device
     */
    @BeforeMethod
    public void setup(Method method, @Optional("desktop") String device) {
    	
    	// Decide device type based on test method name
        String deviceToUse  = method.getName().contains("Device") ? "iPhone14Pro" : "desktop";
    	driver = DriverFactory.createDriver(deviceToUse);

        // Launch app
        driver.get(ConfigReader.get("url"));

        // Create a new test entry in Extent Report
        test = extent.createTest(method.getName());
        Log.setExtentTest(test);
        
        log.info("Browser launched and navigated to U-Ask application");
    }

    /**
     * To quit the browser
     * 
     * @param result
     */
	@AfterMethod
	public void tearDown(ITestResult result) {
		// Capture screenshot on failure
		if (result.getStatus() == ITestResult.FAILURE) {
			takeScreenshot(result.getName());
			test.fail("Test failed: " + result.getThrowable());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			test.pass("Test passed successfully");
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("Test skipped: " + result.getThrowable());
		} // Quit browser after each test
		if (driver != null) {
			driver.quit();
			log.info("Browser closed for test: " + result.getName());
		}
	}

    // ----------------- Screenshot Utility -----------------
    /**
     * To take a screenshot
     * 
     * @param name
     */
    public void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            File dest = new File("screenshots/" + name + ".png");
            dest.getParentFile().mkdirs(); // create folder if not exists
            FileUtils.copyFile(src, dest);
            log.info("Screenshot taken: " + dest.getAbsolutePath());
            if (test != null) test.addScreenCaptureFromPath(dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}