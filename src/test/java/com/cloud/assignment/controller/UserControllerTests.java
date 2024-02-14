package com.cloud.assignment.controller;

import com.cloud.assignment.entity.User;
import com.cloud.assignment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUserTest() throws Exception{
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testUser@gmail.com");
        requestBody.put("password", "password");
        requestBody.put("first_name", "Test");
        requestBody.put("last_name", "User");

        User mockUser = returnMockUser();

        when(bCryptPasswordEncoder.encode(any(CharSequence.class))).thenReturn("hashSaltedPassword");
        when(userService.createUser(any(User.class))).thenReturn(mockUser);
        ResponseEntity<Object> response =  userController.createUser(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    public void getUserTest() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser@gmail.com", "hashSaltedPassword");
        HttpServletRequest httpServletRequest = mockHttpServletRequest(0);
        User mockUser = returnMockUser();
        when(userService.getUser("testUser@gmail.com")).thenReturn(mockUser);
        ResponseEntity<Object> response = userController.getUser(authentication, httpServletRequest, Collections.emptyMap());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    public void updateUserTest() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser@gmail.com", "hashSaltedPassword");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", "newHashSaltedPassword");

        User mockUser = returnMockUser();
        mockUser.setPassword(requestBody.get("password"));
        mockUser.setAccount_updated(LocalDateTime.now());
        when(userService.getUserByUsername("testUser@gmail.com")).thenReturn(mockUser);

        ResponseEntity<Void> response = userController.updateUser(requestBody, authentication);
        User newUser = userService.getUserByUsername("test@example.com");

        assertEquals(mockUser, userService.getUserByUsername("testUser@gmail.com"));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public User returnMockUser() {
        User user = new User();
        user.setUsername("testUser@gmail.com");
        user.setPassword("hashSaltedPassword");
        user.setFirst_name("Test");
        user.setLast_name("User");
        user.setAccount_created(LocalDateTime.now());
        user.setAccount_updated(LocalDateTime.now());

        return user;
    }

    private HttpServletRequest mockHttpServletRequest(int contentLength) {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        when(httpServletRequest.getContentLength()).thenReturn(contentLength);
        return httpServletRequest;
    }



}
