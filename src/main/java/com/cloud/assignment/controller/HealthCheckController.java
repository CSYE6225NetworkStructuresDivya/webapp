package com.cloud.assignment.controller;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void initialize(){
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.set("Pragma", "no-cache");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
    }

    @GetMapping("")
    public ResponseEntity<Void> healthCheck(
            @RequestBody(required = false) String body,
            @RequestParam(required = false) Map<String, String> query) {
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(503).headers(httpHeaders).build();
        }
        if(body != null || query.size() > 0)
            return ResponseEntity.badRequest().headers(httpHeaders).build();

        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS })
    public ResponseEntity<Void> methodNotAllowed() {
        return ResponseEntity.status(405).headers(httpHeaders).build();
    }
}
