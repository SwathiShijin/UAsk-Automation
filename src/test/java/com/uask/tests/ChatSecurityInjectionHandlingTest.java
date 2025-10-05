package com.uask.tests;

import org.testng.annotations.Test;

import com.uask.base.BaseTest;
import com.uask.pages.ChatPage;
import com.uask.pages.LoginPage;

import utils.ChatReportUtils;
import utils.ConfigReader;
import utils.JsonUtils;
import utils.Log;
import utils.TestData;
import utils.TextUtils;

public class ChatSecurityInjectionHandlingTest  extends BaseTest {
	
	String userName = ConfigReader.get("username");
	String password = ConfigReader.get("password");
	
	@Test(priority = 0)
	public void tc01VerifyChatInputSanitization() {
		Log.message("tc01VerifyChatInputSanitization: Verify that special characters are rendered harmlessly.");

		String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginUsingCredential();
			loginPage.loginToUAsk(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			String filePath = "src/test/resources/test-data.json";
			TestData question = JsonUtils.getSecurityTestById(filePath, "SEC_01");

			userQuestion = question.getInput();
			expectedResponse = question.getExpectedFallback();
			threshold = question.getThreshold();

			if (question != null) {
				chatPage.enterChatInput(userQuestion);
				chatPage.clickOnButtonSend();

				actualResponse = chatPage.getLastAIMessage(driver);
				similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
				isValid = similarityScore >= threshold;
				Log.assertThat(isValid,
						"Chat input is sanitized; malicious scripts are not executed.",
			            "Chat input is not sanitized; malicious scripts may be executed.");
			}
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, null, isValid);
		}
	}

	@Test(priority = 1)
	public void tc02VerifyAIIgnoresMaliciousPrompts() {
		Log.message("tc02VerifyAIIgnoresMaliciousPrompts: Verify that AI ignores malicious prompts.");

		String userQuestion = "", expectedResponse = "", actualResponse = "";
		Double threshold, similarityScore = 0.0 ;
		boolean isValid = false;
		
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.clickOnLoginUsingCredential();
			loginPage.loginToUAsk(userName, password);

			ChatPage chatPage = new ChatPage(driver);
			String filePath = "src/test/resources/test-data.json";
			TestData question = JsonUtils.getSecurityTestById(filePath, "SEC_02");

			userQuestion = question.getInput();
			expectedResponse = question.getExpectedFallback();
			threshold = question.getThreshold();

			if (question != null) {
				chatPage.enterChatInput(userQuestion);
				chatPage.clickOnButtonSend();

				actualResponse = chatPage.getLastAIMessage(driver);
				similarityScore = TextUtils.getSemanticSimilarity(expectedResponse, actualResponse);
				isValid = similarityScore >= threshold;
				
				Log.assertThat(isValid,
						"Chat input is sanitized; malicious scripts are not executed.",
			            "Chat input is not sanitized; malicious scripts may be executed.");
			}
		} catch (Exception e) {
			Log.fail("Error while validating semantic similarity: " + e.getMessage());
		} finally {
			ChatReportUtils.logResultHtml(userQuestion, expectedResponse, actualResponse, null, isValid);
		}
	}
}
