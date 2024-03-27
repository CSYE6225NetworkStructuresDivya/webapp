package com.cloud.assignment.controller;

import com.cloud.assignment.service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/verify")
@CrossOrigin(value="*")
public class EmailVerificationController {

    private final UserService userService;
    private HttpHeaders httpHeaders;
    private final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void initialize(){
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.set("Pragma", "no-cache");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
        logger.info("UserController initialized");
    }

    @GetMapping(value = "")
    public ResponseEntity<Object> verifyEmail(@RequestParam Map<String, String> params) throws IOException {
        logger.debug("Received verification request");
        String token = params.get("token");
        String expiresParam = params.get("expires");

        logger.debug("Token: " + token + " Expires: " + expiresParam);

        if(token == null || expiresParam == null) {
            logger.error("Invalid Request: Request has missing token or expires");
            return new ResponseEntity<>(
                    "Invalid Request: Request has missing token or expires",
                    httpHeaders,
                    HttpStatus.BAD_REQUEST
            );
        }

        long expires = Long.parseLong(expiresParam);
        Instant expirationTime = Instant.ofEpochMilli(expires);

        if(Instant.now().isAfter(expirationTime)) {
            logger.error("Verification link expired");
            return new ResponseEntity<>(
                    "Verification link expired",
                    httpHeaders,
                    HttpStatus.BAD_REQUEST
            );
        }
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String receiver = new String(decodedBytes);

        //logic to set user verfied to true in database
        userService.setUserVerified(receiver);
        logger.info("User verified: " + receiver);

        return new ResponseEntity<>(
                "User has been verified",
                httpHeaders,
                HttpStatus.OK
        );
    }
}
