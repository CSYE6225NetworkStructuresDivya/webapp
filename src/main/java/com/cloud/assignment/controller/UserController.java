package com.cloud.assignment.controller;

import com.cloud.assignment.entity.User;
import com.cloud.assignment.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
    private String currentPrincipalName;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void initialize(){
        httpHeaders = new HttpHeaders();
        httpHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.set("Pragma", "no-cache");
        httpHeaders.set("X-Content-Type-Options", "nosniff");
    }

    @PostMapping(value = "/v1/user", consumes = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody Map<String, String> request) throws Exception{
        try {
            String username = request.get("username");
            String password = request.get("password");
            String first_name = request.get("first_name");
            String last_name = request.get("last_name");

            //check if any required parameters are empty
            if(username == null || password == null || first_name == null || last_name == null)
                return new ResponseEntity<>(
                        "Invalid Request: Please provide username, password, first_name and last_name",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );

            //check if the username has a valid regex
            String userNameRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if(!username.matches(userNameRegex))
                return new ResponseEntity<>(
                        "Invalid Request: Username is not a valid email",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );

            //check if password is an empty string
            if(password.isEmpty()) {
                return new ResponseEntity<>(
                        "Password cannot be empty",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );
            }

            //check if user already exists
            User u = userService.getUserByUsername(username);
            if(u != null)
                return new ResponseEntity<>(
                        "Invalid Request: User already exists",
                        httpHeaders,
                        HttpStatus.BAD_REQUEST
                );

            User user = new User();
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));

            return new ResponseEntity<>(
                    userService.createUser(user),
                    httpHeaders,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<Object> getUser(Authentication authentication,
                                          HttpServletRequest httpServletRequest,
                                          @RequestParam Map<String, String> params) {
        //if payload return error
        if(httpServletRequest.getContentLength() > 0 || !params.isEmpty())
            return new ResponseEntity<>(
                    "Invalid Request: No payload or request params accepted",
                    httpHeaders,
                    HttpStatus.BAD_REQUEST
            );

        return ResponseEntity.ok().headers(httpHeaders).body(userService.getUser(authentication.getName()));
    }

    @PutMapping(value = "/v1/user/self", consumes = "application/json")
    public ResponseEntity<Void> updateUser(@RequestBody Map<String, String> request, Authentication authentication) throws Exception {
        try {
            User user = userService.getUserByUsername(authentication.getName());

            for(Map.Entry<String, String> jsonPair : request.entrySet()) {
                String key = jsonPair.getKey();
                if(key.equals("username") || key.equals("account_created") || key.equals("account_updated"))
                    return ResponseEntity.badRequest().headers(httpHeaders).build();
                else if (key.equals("password") || key.equals("first_name") || key.equals("last_name"))
                    continue;
                else
                    return ResponseEntity.badRequest().headers(httpHeaders).build();
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
            return ResponseEntity.noContent().headers(httpHeaders).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().headers(httpHeaders).build();
        }

    }





}
