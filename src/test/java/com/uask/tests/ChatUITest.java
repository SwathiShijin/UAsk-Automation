package com.uask.tests;

import org.testng.annotations.Test;

import com.uask.base.BaseTest;
import com.uask.pages.ChatPage;
import com.uask.pages.LoginPage;

import utils.ConfigReader;
import utils.JsonUtils;
import utils.Log;
import utils.TestData;
import utils.TextUtils;

public class ChatUITest extends BaseTest {
	
	String userName = ConfigReader.get("username");
	String password = ConfigReader.get("password");

	@Test(priority = 0)
	public void tc01VerifyChatWidgetLoadsOnDesktop(){
		Log.message("tc01VerifyChatWidgetLoadsOnDesktop: Verify that the chat widget is visible and accessible on both desktop.");
		LoginPage loginPage = new LoginPage(driver);
        loginPage.clickOnLoginUsingCredential();
        loginPage.loginToUAsk(userName, password);

        ChatPage chatPage = new ChatPage(driver);
        Log.assertThat(chatPage.isPageLoaded(),
        		"Chat widget loaded correctly on desktop",
        		"Chat widget not loaded correctly on desktop");
	}
	
	@Test(priority = 1)
	public void tc02VerifyChatWidgetLoadsOnDevice() {
		Log.message(
				"tc02VerifyChatWidgetLoadsOnMobile: Verify that the chat widget is visible and accessible on both Mobile device.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		Log.assertThat(chatPage.isPageLoaded(), "Chat widget loaded correctly on mobile device",
				"Chat widget not loaded correctly on mobile device");
	}
	
	@Test(priority = 2)
	public void tc03VerifyUserCanSendMessage() {
		Log.message("tc03VerifyUserCanSendMessage: Verify users can type and send messages through the input box");
	    
		LoginPage loginPage = new LoginPage(driver);
        loginPage.clickOnLoginUsingCredential();
        loginPage.loginToUAsk(userName, password);

        ChatPage chatPage = new ChatPage(driver);
        String filePath = "src/test/resources/test-data.json"; 
        TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_01");
        
        String expectedResponse = question.getExpected();
        Double threshold = question.getThreshold();
        
        if (question != null) {
            chatPage.enterChatInput(question.getInput());
            chatPage.clickOnButtonSend();

            String actualResponse = chatPage.getLastAIMessage(driver);
            Log.assertThat(TextUtils.isResponseValid(expectedResponse, actualResponse, threshold), 
            		"User can send messages via input box",
            		"User not able to send messages via input box");
        } 
	}

	@Test(priority = 3)
	public void tc04VerifyAIResponsesRendered() {
		Log.message("tc04VerifyAIResponsesRendered: Verify that AI-generated responses are displayed correctly.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_02");

		String expectedResponse = question.getExpected();
		Double threshold = question.getThreshold();

		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();

			String actualResponse = chatPage.getLastAIMessage(driver);
			Log.assertThat(TextUtils.isResponseValid(expectedResponse, actualResponse, threshold),
					"User can see AI-generated responses are displayed correctly.", 
					"User not able see to AI-generated responses correctly.");
		}
	}

	@Test(priority = 4)
	public void tc05VerifyMultilingualSupport() {
		Log.message("tc05VerifyMultilingualSupport: Verify that AI-generated responses are displayed correctly.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_02");

		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();
			chatPage.getLastAIMessage(driver);

			Log.assertThat(chatPage.isMultilanguageDisplayed("English"),
					"User can able see LTR (English) response",
					"User not able see to LTR (English) response");
		}
		
		question = JsonUtils.getQuestionById(filePath, "UI_AR_01");
		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();
			chatPage.getLastAIMessage(driver);

			Log.assertThat(chatPage.isMultilanguageDisplayed("Arabic"),
					"User can able see RTL (Arabic) response",
					"User not able see to RTL (Arabic) response");
		}

	}

	@Test(priority = 5)
	public void tc06VerifyInputClearedAfterSend() {
		Log.message("tc06VerifyInputClearedAfterSend: Verify that the input box is cleared after sending a message.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question = JsonUtils.getQuestionById(filePath, "UI_EN_03");
		if (question != null) {
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();

			Log.assertThat(chatPage.isInputCleared(), 
					"User can able see input box is cleared after sending the message",
					"User not able see input box is cleared after sending the message");
		}

	}

	@Test(priority = 6)
	public void tc07VerifyScrollAndAccessibility() {
		
		Log.message("tc07VerifyScrollAndAccessibility: Verify that scrolling works correctly and the chat widget is accessible.");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.clickOnLoginUsingCredential();
		loginPage.loginToUAsk(userName, password);

		ChatPage chatPage = new ChatPage(driver);
		String filePath = "src/test/resources/test-data.json";
		TestData question;

		// Send multiple messages to check scroll
		for (int i = 3; i < 5; i++) {
			question = JsonUtils.getQuestionById(filePath, "UI_EN_0"+i);
			chatPage.enterChatInput(question.getInput());
			chatPage.clickOnButtonSend();
			chatPage.getLastAIMessage(driver);
		}

		Log.assertThat(chatPage.verifyScrollExistForResponseContainer(), 
				"Scroll bar is displayed when AI response overflows – Accessibility verified.",
                "Scroll bar not displayed when expected – Accessibility failed.");
	}
}
