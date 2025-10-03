package com.uask.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.*;
import java.util.UUID;

public class APIUtils {

	private static String baseUrl = "https://govgpt.sandbox.dge.gov.ae";
	private static String loginEndPoint = "/api/v1/auths/signin";
	private static String newChatEndPoint = "/api/v1/chats/new";
	public static String aiResponseEndPoint = "/api/chat/completed";

	/**
	 * To get access token
	 * 
	 * @param username
	 * @param password
	 * @return - String as token
	 */
	public static String getAccessToken(String username, String password) {
		Response loginResponse = RestAssured.given().contentType(ContentType.JSON)
				.body("{\"email\":\"" + username + "\", \"password\":\"" + password + "\"}")
				.post(baseUrl + loginEndPoint).then().statusCode(200).extract().response();

		return loginResponse.jsonPath().getString("token");
	}

	/**
	 * To perform 'POST' request
	 * 
	 * @param endpoint
	 * @param token
	 * @param payload
	 * @return - response
	 */
	public static Response postRequest(String endpoint, String token, String payload) {
		return RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(endpoint)
                .then()
                .extract().response();
	}

	/**
	 * To perform 'GET' request
	 * 
	 * @param endpoint
	 * @param token
	 * @return - response
	 */
	public static Response getRequest(String endpoint, String token) {
		return RestAssured.given()
				.baseUri(baseUrl)
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.when()
				.get(endpoint)
				.then()
				.extract().response();
	}

	/**
	 * To generate unique UUID
	 * 
	 * @return - string
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * To create a new chat session
	 *
	 * @param token
	 * @return userMessage
	 */
	public static Map<String, String> createNewChat(String token, String userMessage) {
        String messageId = generateUUID();
        long timestamp = Instant.now().getEpochSecond();

        String payload = "{\n" +
                "  \"chat\": {\n" +
                "    \"id\": \"\",\n" +
                "    \"title\": \"New Chat\",\n" +
                "    \"models\": [\"GovGPT\"],\n" +
                "    \"params\": {},\n" +
                "    \"history\": {\n" +
                "      \"messages\": {\n" +
                "        \"" + messageId + "\": {\n" +
                "          \"id\": \"" + messageId + "\",\n" +
                "          \"parentId\": null,\n" +
                "          \"childrenIds\": [],\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"" + userMessage + "\",\n" +
                "          \"timestamp\": " + timestamp + ",\n" +
                "          \"models\": [\"GovGPT\"],\n" +
                "          \"features\": {\n" +
                "            \"web_search\": false,\n" +
                "            \"deep_search\": false,\n" +
                "            \"rag\": false,\n" +
                "            \"unifyAgent\": false\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"currentId\": \"" + messageId + "\"\n" +
                "    },\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"id\": \"" + messageId + "\",\n" +
                "        \"parentId\": null,\n" +
                "        \"childrenIds\": [],\n" +
                "        \"role\": \"user\",\n" +
                "        \"content\": \"" + userMessage + "\",\n" +
                "        \"timestamp\": " + timestamp + ",\n" +
                "        \"models\": [\"GovGPT\"],\n" +
                "        \"features\": {\n" +
                "          \"web_search\": false,\n" +
                "          \"deep_search\": false,\n" +
                "          \"rag\": false,\n" +
                "          \"unifyAgent\": false\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"tags\": [],\n" +
                "    \"timestamp\": " + (Instant.now().toEpochMilli()) + ",\n" +
                "    \"agent_id\": null\n" +
                "  }\n" +
                "}";
        Response response = postRequest(newChatEndPoint, token, payload);
        response.then().statusCode(200);

        Map<String, String> result = new HashMap<>();
        result.put("chatId", response.jsonPath().getString("id"));
        result.put("sessionId", response.jsonPath().getString("session_id") != null ? response.jsonPath().getString("session_id") : generateUUID());
        result.put("userMessageId", messageId);

        return result;
    }

    /**
     * To get ai response
     * 
     * @param token
     * @param sessionId
     * @param chatId
     * @param userMessage
     * @return
     */
    public static Map<String, String> sendAIResponse(String token, String sessionId, String chatId, String userMessage) {
        String messageId = generateUUID();
        long timestamp = Instant.now().getEpochSecond();

        String payload = "{\n" +
                "  \"chat_id\": \"" + chatId + "\",\n" +
                "  \"session_id\": \"" + sessionId + "\",\n" +
                "  \"messages\": [{\n" +
                "    \"id\": \"" + messageId + "\",\n" +
                "    \"role\": \"user\",\n" +
                "    \"content\": \"" + userMessage + "\",\n" +
                "    \"timestamp\": " + timestamp + "\n" +
                "  }]\n" +
                "}";

        Response response = postRequest(aiResponseEndPoint, token, payload);
        response.then().statusCode(200);

        Map<String, String> result = new HashMap<>();
        result.put("assistantResponse", response.jsonPath().getString("messages[1].content"));
        result.put("assistantMessageId", response.jsonPath().getString("messages[1].id"));
        result.put("chatId", response.jsonPath().getString("chat_id"));
        result.put("sessionId", response.jsonPath().getString("session_id"));

        return result;
    }
}
