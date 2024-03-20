package com.cloud.assignment.security;

import com.cloud.assignment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AssignmentSecurityConfiguration {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(AssignmentSecurityConfiguration.class);

    public AssignmentSecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        logger.info("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((request) -> request
                .requestMatchers("/healthz", "/v1/user").permitAll()
                .anyRequest().authenticated()
        ).httpBasic(Customizer.withDefaults());
        logger.info("Creating SecurityFilterChain bean");
        return httpSecurity.build();
    }
}
