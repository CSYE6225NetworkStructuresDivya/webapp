package com.cloud.assignment.service;

import com.cloud.assignment.entity.User;
import com.cloud.assignment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository theUserRepository) {
        userRepository = theUserRepository;
        logger.info("User Service is created");
    }

    public User getUserByUsername(String username) {
        logger.info("Getting user by username: " + username);
        return userRepository.findByUsername(username);
    }

    public User createUser(User user){
        logger.info("Creating user: " + user);
        return userRepository.save(user);
    }

    public User getUser(String username) {
        logger.info("Getting user by username: " + username);
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user) throws Exception {
        try {
            userRepository.save(user);
            logger.info("User updated in database: " + user);
        } catch (Exception e) {
            logger.error("Error updating user in database: " + e);
            throw new Exception(e);
        }
    }

    public User setUserVerified(String username) {
        User user = userRepository.findByUsername(username);
        user.setVerified(true);
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: " + username);
        return userRepository.findByUsername(username);
    }
}
