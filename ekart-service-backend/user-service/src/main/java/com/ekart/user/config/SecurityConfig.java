package com.ekart.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF completely for all endpoints
            .csrf(AbstractHttpConfigurer::disable)
            
            // Disable CORS (API Gateway handles CORS)
            .cors(AbstractHttpConfigurer::disable)
            
            // Stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Allow all requests (API Gateway handles authentication)
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            
            // Disable default security headers since API Gateway handles this
            .headers(headers -> headers.frameOptions().deny());

        return http.build();
    }
}