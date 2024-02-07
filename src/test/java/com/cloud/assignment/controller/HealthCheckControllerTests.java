package com.cloud.assignment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HealthCheckControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void healthCheckTest() {
        String url = "http://localhost:8080/healthz";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());

        String urlWithParams = "http://localhost:8080/healthz?name=Divya";
        ResponseEntity<String> response1 = restTemplate.getForEntity(urlWithParams, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(null, response1.getBody());
    }

    @Test
    public void methodNotAllowedTest() {
        String url = "http://localhost:8080/healthz";
        ResponseEntity<String> response1 = restTemplate.postForEntity(url, String.class, String.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response1.getStatusCode());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response2 = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response2.getStatusCode());

        //ResponseEntity<String> response3 = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
        //assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response3.getStatusCode());

        ResponseEntity<String> response4 = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response4.getStatusCode());

    }
}
