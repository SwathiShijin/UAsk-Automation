package com.uask.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.uask.base.APIUtils;

import static io.restassured.RestAssured.*;

import java.util.Map;

import io.restassured.response.Response;
import utils.ConfigReader;
import utils.JsonUtils;
import utils.Log;
import utils.TestData;
import utils.TextUtils;
public class ChatAPITest {

    String filePath = "src/test/resources/test-data.json";
    String username = ConfigReader.get("username");
    String password = ConfigReader.get("password");
    
    private String token;

    @BeforeClass
    public void setup() {
        token = APIUtils.getAccessToken(username, password);
        //sessionId = APIUtils.generateUUID(); 
    }
    
    @Test(priority = 0)
    public void tc01VerifyHelpfulResponse() {
    	
    	Log.message("tc01VerifyHelpfulResponse: Verify that user able to see helpful ai response");
    	
    	TestData questionId = JsonUtils.getQuestionById(filePath, "UI_EN_01");
        
        Map<String, String> chatData = APIUtils.createNewChat(token, questionId.getInput());
        String chatId = chatData.get("chatId");
        String sessionId = chatData.get("sessionId");

        Map<String, String> responseData = APIUtils.sendAIResponse(token, sessionId, chatId, questionId.getInput());
        String actualResponse = responseData.get("assistant");

        Log.assertThat(TextUtils.isResponseValid(questionId.getExpected(), actualResponse, questionId.getThreshold()), 
        		"Response is clear and helpful response to common public service queries",
        		"Response not clear and helpful enough!\nExpected: " + questionId.getExpected() + "\nActual: " + actualResponse);
    }

    @Test(priority = 1)
    public void tc02VerifyNoHallucination() {
    	
    	Log.message("tc02VerifyNoHallucination: Verify that user able to see proper response, not hallucinated");
    	
        TestData questionId = JsonUtils.getQuestionById("src/test/resources/testdata.json", "UI_EN_02");

        String payload = "{ \"model\":\"GovGPT\", \"chat_id\":\"" + sessionId + "\", " +
                "\"messages\":[{\"id\":\"" + APIUtils.generateUUID() + "\", \"role\":\"user\", " +
                "\"content\":\"" + questionId.getInput() + "\"}]}";

        Response response = APIUtils.postRequest(APIUtils.aiResponseEndPoint, token, payload);
        String actualResponse = response.jsonPath().getString("messages[1].content");

        Log.assertThat(TextUtils.isResponseValid(questionId.getExpected(), actualResponse, questionId.getThreshold()), 
        		"Responses are not hallucinated ",
        		"Hallucination detected! Response irrelevant.\nExpected: " +
        				questionId.getExpected() + "\nActual: " + actualResponse);
    }

    @Test(priority = 2)
    public void tc03VerifyResponseConsistencyAcrossLanguages() {
    	
    	Log.message("tc03VerifyResponseConsistencyAcrossLanguages: Verify that user able to see that responses stay consistent across languages(Arabic and English)");
    	
        TestData responseEnglish = JsonUtils.getQuestionById("src/test/resources/testdata.json", "UI_EN_05");
        TestData responseArabic = JsonUtils.getQuestionById("src/test/resources/testdata.json", "UI_AR_01");

        String payloadEnglish = "{ \"model\":\"GovGPT\", \"chat_id\":\"" + sessionId + "\", " +
                "\"messages\":[{\"id\":\"" + APIUtils.generateUUID() + "\", \"role\":\"user\", " +
                "\"content\":\"" + responseEnglish.getInput() + "\"}]}";

        String payloadArabic = "{ \"model\":\"GovGPT\", \"chat_id\":\"" + sessionId + "\", " +
                "\"messages\":[{\"id\":\"" + APIUtils.generateUUID() + "\", \"role\":\"user\", " +
                "\"content\":\"" + responseArabic.getInput() + "\"}]}";

        Response resEn = APIUtils.postRequest(APIUtils.aiResponseEndPoint, token, payloadEnglish);
        Response resAr = APIUtils.postRequest(APIUtils.aiResponseEndPoint, token, payloadArabic);

        String actualEn = resEn.jsonPath().getString("messages[1].content");
        String actualAr = resAr.jsonPath().getString("messages[1].content");

        Log.assertThat(TextUtils.isResponseValid(responseEnglish.getExpected(), actualEn, responseEnglish.getThreshold())
        		&& TextUtils.isResponseValid(responseArabic.getExpected(), actualAr, responseArabic.getThreshold()), 
        		"AI response consistent across english and arabic",
        		"Inconsistent responses across EN/AR!\nEN: " + actualEn + "\nAR: " + actualAr);
    }

    @Test(priority = 3)
    public void tc04VerifyCleanFormatting() {
    	
    	Log.message("tc04VerifyCleanFormatting: Verify that user able to see clean response formatting");
    	
        TestData questionId = JsonUtils.getQuestionById("src/test/resources/testdata.json", "UI_EN_03");

        String payload = "{ \"model\":\"GovGPT\", \"chat_id\":\"" + sessionId + "\", " +
                "\"messages\":[{\"id\":\"" + APIUtils.generateUUID() + "\", \"role\":\"user\", " +
                "\"content\":\"" + questionId.getInput() + "\"}]}";

        Response response = APIUtils.postRequest(APIUtils.aiResponseEndPoint, token, payload);
        String actualResponse = response.jsonPath().getString("messages[1].content");

        Log.assertThat(actualResponse.contains("<script>"), 
        		"XSS vulnerability not detected!",
        		"XSS vulnerability detected!");
        
        Log.assertThat(actualResponse.endsWith("...") && actualResponse.length() > 50, 
        		"Response look complete and helpful",
        		"Response look incomplete and unhelpful");
    }

    @Test(priority = 4)
    public void tc05VerifyFallbackMessage() {
    	
    	Log.message("tc05VerifyFallbackMessage: Verify that user able to see proper fallback messages");
    	
        TestData questionId = JsonUtils.getSecurityTestById("src/test/resources/testdata.json", "SEC_01");
        String expectedResponse = questionId.getExpectedFallback().trim();

        String payload = "{ \"model\":\"GovGPT\", \"chat_id\":\"" + sessionId + "\", " +
                "\"messages\":[{\"id\":\"" + APIUtils.generateUUID() + "\", \"role\":\"user\", " +
                "\"content\":\"" + questionId.getInput() + "\"}]}";

        Response response = APIUtils.postRequest(APIUtils.aiResponseEndPoint, token, payload);
        String actualResponse = response.jsonPath().getString("messages[1].content");

        Log.assertThat(actualResponse.trim().contains(expectedResponse), 
        		"Fallback message is displayed properly as expected",
        		"Fallback message incorrect!\nExpected: " + expectedResponse + "\nActual: " + actualResponse);
    }

}
