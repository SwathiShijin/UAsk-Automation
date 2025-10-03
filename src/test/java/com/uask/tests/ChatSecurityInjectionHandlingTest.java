package com.uask.tests;

import org.testng.annotations.Test;

import com.uask.base.BaseTest;
import com.uask.pages.ChatPage;
import com.uask.pages.LoginPage;

import utils.ConfigReader;
import utils.JsonUtils;
import utils.Log;
import utils.TestData;

public class ChatSecurityInjectionHandlingTest  extends BaseTest {
	
	String userName = ConfigReader.get("username");
	String password = ConfigReader.get("password");
	
	@Test(priority = 0)
	public void tc01VerifyChatInputSanitization() {
		Log.message("tc01VerifyChatInputSanitization: Verify that special characters are rendered harmlessly.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question = JsonUtils.getSecurityTestById(filePath, "SEC_01");

		String expectedResponse = question.getExpectedFallback();

		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();

			String actualResponse = chatPage.getLastAIMessage(driver);
			Log.assertThat(actualResponse.contains(expectedResponse),
					"Chat input is sanitized; malicious scripts are not executed.",
		            "Chat input is not sanitized; malicious scripts may be executed.");
		}
	}

	@Test(priority = 1)
	public void tc02VerifyAIIgnoresMaliciousPrompts() {
		Log.message("tc02VerifyAIIgnoresMaliciousPrompts: Verify that AI ignores malicious prompts.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question = JsonUtils.getSecurityTestById(filePath, "SEC_02");

		String expectedResponse = question.getExpectedFallback();

		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();

			String actualResponse = chatPage.getLastAIMessage(driver);
			Log.assertThat(actualResponse.contains(expectedResponse),
					"Chat input is sanitized; malicious scripts are not executed.",
		            "Chat input is not sanitized; malicious scripts may be executed.");
		}
	}
}
