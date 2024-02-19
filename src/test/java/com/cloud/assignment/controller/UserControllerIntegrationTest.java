package com.cloud.assignment.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerIntegrationTest {

    @Test
    public void test1() {
        RestAssured.baseURI = "http://localhost:8080";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "test@example.com");
        requestBody.put("password", "password");
        requestBody.put("first_name", "Test");
        requestBody.put("last_name", "User");

        Response postResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/user");

        assertEquals(201, postResponse.getStatusCode());
        assertEquals("test@example.com", postResponse.getBody().jsonPath().get("username"));

        Response getResponse = RestAssured.given()
                .auth().basic("test@example.com", "password")
                .get("/v1/user/self");

        assertEquals(200, getResponse.getStatusCode());
        assertEquals("test@example.com", getResponse.getBody().jsonPath().get("username"));
    }

    @Test
    public void test2() {
        RestAssured.baseURI = "http://localhost:8080";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("first_name", "UpdatedFirstName");
        requestBody.put("last_name", "UpdatedLastName");

        // Send PUT request to update user data
        Response putResponse = RestAssured.given()
                .auth().basic("test@example.com", "password")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .put("/v1/user/self");

        assertEquals(204, putResponse.getStatusCode());

        Response getResponse = RestAssured.given()
                .auth().basic("test@example.com", "password")
                .get("/v1/user/self");

        assertEquals(200, getResponse.getStatusCode());
        assertEquals("UpdatedFirstName", getResponse.getBody().jsonPath().get("first_name"));
        assertEquals("UpdatedLastName", getResponse.getBody().jsonPath().get("last_name"));
    }
}
