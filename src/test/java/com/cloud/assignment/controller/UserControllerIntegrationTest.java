package com.cloud.assignment.controller;

import com.cloud.assignment.AssignmentApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {AssignmentApplication.class})
public class UserControllerIntegrationTest {

    private static String userIdentifier;

    @BeforeEach
    public void setUp() {
        if (userIdentifier == null) {
            userIdentifier = "testUser" + System.currentTimeMillis();
        }
    }

    @Test
    public void test1() {
        RestAssured.baseURI = "http://localhost:8080";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", this.userIdentifier + "@example.com");
        requestBody.put("password", "password");
        requestBody.put("first_name", "Test");
        requestBody.put("last_name", "User");

        Response postResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/user");

        assertEquals(201, postResponse.getStatusCode());
        assertEquals(this.userIdentifier + "@example.com", postResponse.getBody().jsonPath().get("username"));

        Response getResponse = RestAssured.given()
                .auth().basic(this.userIdentifier + "@example.com", "password")
                .get("/v1/user/self");

        assertEquals(200, getResponse.getStatusCode());
        assertEquals(this.userIdentifier + "@example.com", getResponse.getBody().jsonPath().get("username"));
    }

    @Test
    public void test2() {
        RestAssured.baseURI = "http://localhost:8080";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("first_name", "UpdatedFirstName");
        requestBody.put("last_name", "UpdatedLastName");

        // Send PUT request to update user data
        Response putResponse = RestAssured.given()
                .auth().basic(this.userIdentifier + "@example.com", "password")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .put("/v1/user/self");

        assertEquals(204, putResponse.getStatusCode());

        Response getResponse = RestAssured.given()
                .auth().basic(this.userIdentifier + "@example.com", "password")
                .get("/v1/user/self");

        assertEquals(200, getResponse.getStatusCode());
        assertEquals("UpdatedFirstName", getResponse.getBody().jsonPath().get("first_name"));
        assertEquals("UpdatedLastName", getResponse.getBody().jsonPath().get("last_name"));
    }
}
