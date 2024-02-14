package com.cloud.assignment.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class HealthCheckControllerTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void healthCheckTest() {
        given().get("/healthz")
                .then()
                .statusCode(200);

        given().param("name", "Divya")
                .get("/healthz")
                .then()
                .statusCode(400);
    }

    @Test
    public void methodNotAllowedTest() {
        given().post("/healthz")
                .then()
                .statusCode(405);

        given().put("/healthz")
                .then()
                .statusCode(405);

        given().request("PATCH", "/healthz")
                .then()
                .statusCode(405);

        given().delete("/healthz")
                .then()
                .statusCode(405);
    }
}
