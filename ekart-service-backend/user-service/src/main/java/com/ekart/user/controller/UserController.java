package com.ekart.user.controller;

import com.ekart.common.dto.ApiResponse;
import com.ekart.user.dto.*;
import com.ekart.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("User registration request for email: {}", registrationDto.getEmail());
        
        UserDto user = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("User login request for email: {}", loginDto.getEmail());
        
        AuthResponseDto authResponse = userService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
    }

    @GetMapping("/users/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfile(@RequestParam String email) {
        log.info("Get user profile request for email: {}", email);
        
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user, "User profile retrieved successfully"));
    }

    @PutMapping("/users/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserProfile(
            @RequestParam String email, 
            @Valid @RequestBody UserUpdateDto updateDto) {
        log.info("Update user profile request for email: {}", email);
        
        UserDto updatedUser = userService.updateUserProfile(email, updateDto);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User profile updated successfully"));
    }

    @PostMapping("/users/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String email, 
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        log.info("Change password request for email: {}", email);
        
        userService.changePassword(email, passwordChangeDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        log.info("Get all users request");
        
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/users/{keycloakId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserByKeycloakId(@PathVariable String keycloakId) {
        log.info("Get user by Keycloak ID request: {}", keycloakId);
        
        UserDto user = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }
}
