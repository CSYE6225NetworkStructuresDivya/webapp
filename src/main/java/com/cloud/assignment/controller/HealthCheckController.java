package com.cloud.assignment.controller;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

@RestController
@CrossOrigin(value="*")
@RequestMapping("/healthz")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;
    private HttpHeaders httpHeaders;

    private final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @PostConstruct
    public void initialize(){
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.set("Pragma", "no-cache");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
        logger.info("HealthCheckController initialized");
    }

    @GetMapping("")
    public ResponseEntity<Void> healthCheck(
            @RequestBody(required = false) String body,
            @RequestParam(required = false) Map<String, String> query) {
        try {
            dataSource.getConnection();
            logger.info("Getting connection from dataSource");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error getting connection from dataSource" + e.getMessage());
            return ResponseEntity.status(503).headers(httpHeaders).build();
        }
        if(body != null || query.size() > 0) {
            logger.error("No connection from dataSource");
            return ResponseEntity.badRequest().headers(httpHeaders).build();
        }
        logger.info("Connection from dataSource successful");
        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS })
    public ResponseEntity<Void> methodNotAllowed() {
        logger.error("POST, PUT, DELETE, PATCH, HEAD, OPTIONS methods not allowed for /healthz");
        return ResponseEntity.status(405).headers(httpHeaders).build();
    }
}
