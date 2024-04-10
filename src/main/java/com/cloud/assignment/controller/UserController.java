package com.cloud.assignment.controller;

import com.cloud.assignment.entity.User;
import com.cloud.assignment.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(value="*")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private HttpHeaders httpHeaders;
    private final PubSubPublisher pubSubPublisher;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, PubSubPublisher pubSubPublisher) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.pubSubPublisher = pubSubPublisher;
    }

    @PostConstruct
    public void initialize(){
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.set("Pragma", "no-cache");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
        logger.info("UserController initialized");
    }

    @PostMapping(value = "/v1/user", consumes = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody Map<String, String> request) throws Exception{
        logger.info("Request body to create user: " + request);
        try {
            String username = request.get("username");
            String password = request.get("password");
            String first_name = request.get("first_name");
            String last_name = request.get("last_name");

            //check if any required parameters are empty
            if(username == null || password == null || first_name == null || last_name == null) {
                logger.error("Invalid Request: Request has missing username, password, first_name and last_name");
                return new ResponseEntity<>(
                        "Invalid Request: Please provide username, password, first_name and last_name",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );
            }
            logger.info("Request has all required parameters");

            //check if the username has a valid regex
            String userNameRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if(!username.matches(userNameRegex)) {
                logger.error("Invalid Request: Username is not a valid email");
                return new ResponseEntity<>(
                        "Invalid Request: Username is not a valid email",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );
            }
            logger.info("Username is a valid email");

            //check if password is an empty string
            if(password.isEmpty()) {
                logger.error("Invalid Request: Password cannot be empty");
                return new ResponseEntity<>(
                        "Password cannot be empty",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );
            }
            logger.info("Password is not empty");

            //check if user already exists
            User u = userService.getUserByUsername(username);
            if(u != null) {
                logger.error("Invalid Request: User already exists");
                return new ResponseEntity<>(
                        "Invalid Request: User already exists",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );
            }
            logger.info("User does not exist in the database");

            User user = new User();
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));

            logger.info("Creating user: " + user.toString());
            pubSubPublisher.publishMessage(username);

            return new ResponseEntity<>(
                    userService.createUser(user),
                    httpHeaders,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            logger.error("Error creating user: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<Object> getUser(Authentication authentication,
                                          HttpServletRequest httpServletRequest,
                                          @RequestParam Map<String, String> params) {
        logger.info("Request params for getting a user: " + params);
        logger.info("Request payload for getting a user: " + httpServletRequest.toString());
        //if payload return error
        if(httpServletRequest.getContentLength() > 0 || !params.isEmpty()) {
            logger.error("Invalid Request: No payload or request params accepted");
            return new ResponseEntity<>(
                    "Invalid Request: No payload or request params accepted",
                    httpHeaders,
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = userService.getUser(authentication.getName());
        logger.info("User found: " + user.toString());

        boolean verified = user.isVerified();
        if(!verified) {
            logger.error("Invalid Request: User is not verified");
            return new ResponseEntity<>(
                    "Invalid Request: User is not verified",
                    httpHeaders,
                    HttpStatus.FORBIDDEN
            );
        }

        return ResponseEntity.ok().headers(httpHeaders).body(user);
    }

    @PutMapping(value = "/v1/user/self", consumes = "application/json")
    public ResponseEntity<Object> updateUser(@RequestBody Map<String, String> request, Authentication authentication) throws Exception {
        logger.info("Request body to update user: " + request);
        try {
            User user = userService.getUserByUsername(authentication.getName());

            boolean verified = user.isVerified();
            if(!verified) {
                logger.error("Invalid Request: User is not verified");
                return new ResponseEntity<>(
                        "Invalid Request: User is not verified",
                        httpHeaders,
                        HttpStatus.FORBIDDEN
                );
            }

            logger.info("User to be updated: " + user.toString());
            for(Map.Entry<String, String> jsonPair : request.entrySet()) {
                String key = jsonPair.getKey();
                if(key.equals("username") || key.equals("account_created") || key.equals("account_updated")) {
                    logger.error("Invalid Request: username, account_created and account_updated cannot be updated");
                    return ResponseEntity.badRequest().headers(httpHeaders).build();
                }
                else if (key.equals("password") || key.equals("first_name") || key.equals("last_name"))
                    continue;
                else {
                    logger.error("Invalid Request: Invalid key " + key + " in request body");
                    return ResponseEntity.badRequest().headers(httpHeaders).build();
                }
            }

            String password = request.get("password");
            String first_name = request.get("first_name");
            String last_name = request.get("last_name");

            if(password != null)
                user.setPassword(bCryptPasswordEncoder.encode(password));
            if(first_name != null)
                user.setFirst_name(first_name);
            if(last_name != null)
                user.setLast_name(last_name);

            userService.updateUser(user);
            logger.info("User updated: " + user.toString());
            return ResponseEntity.noContent().headers(httpHeaders).build();
        } catch (Exception e) {
            logger.error("Error updating user: " + e.getMessage());
            return ResponseEntity.badRequest().headers(httpHeaders).build();
        }

    }
}
