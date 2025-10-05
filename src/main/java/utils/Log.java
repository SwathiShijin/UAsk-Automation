package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;
import org.testng.Reporter;

public class Log {
    private static final Logger log = LogManager.getLogger(Log.class);

    /** Optional: ExtentTest instance for reporting to ExtentReports */
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Set ExtentTest instance for the current thread/test
     */
    public static void setExtentTest(ExtentTest test) {
        extentTest.set(test);
    }

    /**
     * Get current thread's ExtentTest
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * To print the given message
     * 
     * @param message
     * 		- The text message to log
     */
    public static void message(String message) {
		log.info(message);
		if (getTest() != null)
			getTest().log(Status.INFO, message);
		Reporter.log(message + "<br>");
		System.out.println(message);
    }

    /**
     * To log a debug event
     * 
     * @param message
     * 		- The debug message to log
     */
    public static void event(String message) {
        log.debug(message);
		if (getTest() != null)
			getTest().log(Status.INFO, message);
    }

    /**
     * To logs an error-level event message to Log4j.
     * 
     * @param message 
     * 		- The error message to log
     */
    public static void errorEvent(String message) {
        log.error(message);
        if (getTest() != null)
            getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
        Reporter.log("<span style='color:red;'>" + message + "</span><br>");
        System.out.println("\u001B[31m" + message + "\u001B[0m"); // red in console
    }
    
    /**
     * To logs an error-level event message
     * 
     * @param message
     * 		- message to print
     * @param throwable
     */
    public static void errorEvent(String message, Throwable throwable) {
        log.error(message, throwable);
        if (getTest() != null) {
            getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
            getTest().fail(throwable);
        }
        Reporter.log("<span style='color:red;'>" + message + "</span><br>");
        System.out.println("\u001B[31m" + message + "\u001B[0m"); // red in console
    }
    
    /**
     * To pass the log with green highlight
     * 
     * @param message
     * 		- message to print
     */
    public static void pass(String message) {
    	 log.info("[PASS] " + message);
         if (getTest() != null)
             getTest().log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
         Reporter.log("<span style='color:green;'>" + message + "</span><br>");
         System.out.println("\u001B[32m" + message + "\u001B[0m"); // green in console
    }

    /**
     * To logs a failure message and immediately fails the test.
     * 
     * @param message 
     * 		- the failure message to log and report
     */
    public static void fail(String message) {
    	log.error("[FAIL] " + message);
        if (getTest() != null)
            getTest().log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
        Reporter.log("<span style='color:red;'>" + message + "</span><br>");
        System.out.println("\u001B[31m" + message + "\u001B[0m"); // red in console
    }

    /**
     * To logs a warning message to console, Log4j, and ExtentReports.
     * 
     * @param message 
     * 		- The warning message to log
     */
    public static void warnEvent(String message) {
    	log.warn("[WARN] " + message);
        if (getTest() != null)
            getTest().log(Status.WARNING, MarkupHelper.createLabel(message, ExtentColor.ORANGE));
        Reporter.log("<span style='color:orange;'>" + message + "</span><br>");
        System.out.println("\u001B[33m" + message + "\u001B[0m"); // yellow in console
    }

    // ===== Assertions with Logging =====

    /**
     * To asserts a boolean condition and logs the result.
     *
     * @param condition   
     * 		- The boolean condition to check. If true, the test passes; otherwise, it fails.
     * @param passMessage 
     * 		- The message to log if the condition is true.
     * @param failMessage 
     * 		- The message to log and fail the test if the condition is false.
     */
    public static void assertThat(boolean condition, String passMessage, String failMessage) {
    	
    	ExtentTest test = extentTest.get();
        if (condition) {
            log.info(passMessage);
            if (test != null) {
                test.log(Status.PASS, passMessage);
            }
            Reporter.log("<span>" + passMessage + "</span><br>");
            System.out.println(passMessage);
            Assert.assertTrue(true, passMessage);
        } else {
            log.error(failMessage);
            if (test != null) {
                test.log(Status.FAIL, MarkupHelper.createLabel(failMessage, ExtentColor.RED));
            }
            Reporter.log("<span style='color:red;'>" + failMessage + "</span><br>");
            System.out.println(failMessage);
            Assert.fail(failMessage);
        }
    }

    /**
     * To asserts that a boolean condition is true and logs the result.
     * 
     * @param condition 
     * 		- The boolean condition expected to be true
     * @param message   
     * 		- The message describing the assertion
     */
    public static void assertTrue(boolean condition, String message) {
        assertThat(condition, message, "Assertion failed: " + message);
    }

    /**
     * To asserts that a boolean condition is false and logs the result.
     * 
     * @param condition 
     * 		- The boolean condition expected to be false
     * @param message   
     * 		- The message describing the assertion
     */
    public static void assertFalse(boolean condition, String message) {
        assertThat(!condition, message, "Assertion failed: " + message);
    }

    /**
     * To asserts that two objects are equal and logs the result.
     * 
     * @param actual   
     * 		- The actual object value
     * @param expected 
     * 		- The expected object value
     * @param message  
     * 		- The message describing the assertion
     */
    public static void assertEquals(Object actual, Object expected, String message) {
    	
    	ExtentTest test = extentTest.get(); 
    	String fullMessage = message + " | Expected = " + expected + ", Actual = " + actual;
    	
        if ((actual == null && expected == null) || (actual != null && actual.equals(expected))) {
            log.info(fullMessage);
            if (test != null) {
                test.log(Status.PASS, fullMessage);  // Correctly call on ExtentTest instance
            }
            Reporter.log("<span style='color:green;'>" + fullMessage + "</span><br>");
            System.out.println( fullMessage);
            Assert.assertEquals(actual, expected, message);
        } else {
            log.error(fullMessage);
            if (test != null) {
                test.log(Status.FAIL, MarkupHelper.createLabel(fullMessage, ExtentColor.RED));
            }
            Reporter.log("<span style='color:red;'>" + fullMessage + "</span><br>");
            System.out.println(fullMessage);
            Assert.fail(fullMessage);
        }
    }
}
