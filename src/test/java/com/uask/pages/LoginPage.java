package com.uask.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.uask.base.UAskUtils;

import utils.Log;


public class LoginPage {

	WebDriver driver;
	
	@FindBy(xpath = "//button[text()='Login using Credentials']")
    WebElement lnkLoginUsingCredential;
	
	@FindBy(id = "email")
    WebElement txtEmail;
	
	@FindBy(id = "password")
    WebElement txtPassWord;
	
	@FindBy(xpath = "//button[text()='Sign in']")
    WebElement btnSignIn;
	
	public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        UAskUtils.waitForPageLoad(driver, d -> lnkLoginUsingCredential.isDisplayed());
	}
        
	/**
	 * To click on Login Using Credential
	 * 
	 */
	public void clickOnLoginUsingCredential() {
		Log.event("Clicking on Login Using Credential");
		lnkLoginUsingCredential.click();
		Log.message("Clicked on Login Using Credential"); 
	}
	
	/**
	 * To login in to U-Ask application
	 * 
	 * @param email
	 * 		- Email/phone number
	 * @param password
	 * 		- password
	 */
	public void loginToUAsk(String email, String password) {
		Log.event("Login to the U-Ask");
		txtEmail.sendKeys(email);
		txtPassWord.sendKeys(password);
		btnSignIn.click();
		Log.message("Logged into U-Ask as (" + email + "/" + password + ")"); 
    }
}
