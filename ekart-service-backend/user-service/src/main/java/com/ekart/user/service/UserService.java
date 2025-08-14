package com.ekart.user.service;

import com.ekart.user.dto.*;
import com.ekart.user.entity.User;
import com.ekart.user.exception.UserAlreadyExistsException;
import com.ekart.user.exception.UserNotFoundException;
import com.ekart.user.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Transactional
    @CircuitBreaker(name = "user-service", fallbackMethod = "registerUserFallback")
    @Retry(name = "user-service")
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering user with email: {}", registrationDto.getEmail());
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }

        try {
            // Create user in Keycloak
            String keycloakUserId = createKeycloakUser(registrationDto);
            
            // Create user in local database
            User user = new User();
            user.setKeycloakId(keycloakUserId);
            user.setEmail(registrationDto.getEmail());
            user.setFirstName(registrationDto.getFirstName());
            user.setLastName(registrationDto.getLastName());
            user.setPhoneNumber(registrationDto.getPhoneNumber());
            user.setRole(User.Role.USER);
            
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with ID: {}", savedUser.getId());
            
            return convertToDto(savedUser);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "loginFallback")
    @Retry(name = "user-service")
    public AuthResponseDto login(LoginDto loginDto) {
        log.info("User login attempt for email: {}", loginDto.getEmail());
        
        try {
            // Authenticate with Keycloak
            Map<String, Object> tokenResponse = authenticateWithKeycloak(loginDto);
            
            // Get user from database
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            
            AuthResponseDto response = new AuthResponseDto();
            response.setAccessToken((String) tokenResponse.get("access_token"));
            response.setRefreshToken((String) tokenResponse.get("refresh_token"));
            response.setExpiresIn(((Number) tokenResponse.get("expires_in")).longValue());
            response.setUser(convertToDto(user));
            
            log.info("User logged in successfully: {}", user.getEmail());
            return response;
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return convertToDto(user);
    }

    public UserDto getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User not found with Keycloak ID: " + keycloakId));
        return convertToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    private String createKeycloakUser(UserRegistrationDto registrationDto) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(registrationDto.getEmail());
        userRepresentation.setUsername(registrationDto.getEmail());
        userRepresentation.setFirstName(registrationDto.getFirstName());
        userRepresentation.setLastName(registrationDto.getLastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        // Create user
        Response response = usersResource.create(userRepresentation);
        
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak");
        }

        String userId = extractUserIdFromLocation(response.getLocation().toString());

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registrationDto.getPassword());
        credential.setTemporary(false);

        usersResource.get(userId).resetPassword(credential);

        return userId;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> authenticateWithKeycloak(LoginDto loginDto) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", loginDto.getEmail());
        map.add("password", loginDto.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Object> response = restTemplate.postForEntity(tokenUrl, request, Object.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Authentication failed");
        }

        return (Map<String, Object>) response.getBody();
    }

    private String extractUserIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setZipCode(user.getZipCode());
        dto.setCountry(user.getCountry());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @Transactional
    public UserDto updateUserProfile(String email, UserUpdateDto updateDto) {
        log.info("Updating user profile for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        // Update user fields
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getAddress() != null) {
            user.setAddress(updateDto.getAddress());
        }
        if (updateDto.getCity() != null) {
            user.setCity(updateDto.getCity());
        }
        if (updateDto.getState() != null) {
            user.setState(updateDto.getState());
        }
        if (updateDto.getZipCode() != null) {
            user.setZipCode(updateDto.getZipCode());
        }
        if (updateDto.getCountry() != null) {
            user.setCountry(updateDto.getCountry());
        }
        
        User savedUser = userRepository.save(user);
        
        // Also update in Keycloak
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            org.keycloak.representations.idm.UserRepresentation keycloakUser = 
                usersResource.get(user.getKeycloakId()).toRepresentation();
            
            keycloakUser.setFirstName(savedUser.getFirstName());
            keycloakUser.setLastName(savedUser.getLastName());
            
            usersResource.get(user.getKeycloakId()).update(keycloakUser);
            log.info("User profile updated in Keycloak for user: {}", email);
        } catch (Exception e) {
            log.warn("Failed to update user profile in Keycloak: {}", e.getMessage());
        }
        
        return convertToDto(savedUser);
    }

    @Transactional
    public void changePassword(String email, PasswordChangeDto passwordChangeDto) {
        log.info("Changing password for user: {}", email);
        
        // Validate that new password matches confirm password
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        try {
            // Verify current password by attempting login
            MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
            loginRequest.add("grant_type", "password");
            loginRequest.add("client_id", clientId);
            loginRequest.add("client_secret", clientSecret);
            loginRequest.add("username", email);
            loginRequest.add("password", passwordChangeDto.getCurrentPassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(loginRequest, headers);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                    request,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Update password in Keycloak
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(passwordChangeDto.getNewPassword());
            credential.setTemporary(false);
            
            usersResource.get(user.getKeycloakId()).resetPassword(credential);
            log.info("Password changed successfully for user: {}", email);
            
        } catch (Exception e) {
            log.error("Failed to change password for user: {}", email, e);
            if (e.getMessage().contains("Current password is incorrect")) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            throw new RuntimeException("Failed to change password");
        }
    }

    // Fallback methods for circuit breaker
    public UserDto registerUserFallback(UserRegistrationDto registrationDto, Exception ex) {
        log.error("Circuit breaker activated for user registration: {}", ex.getMessage());
        throw new RuntimeException("User registration service is temporarily unavailable");
    }

    public AuthResponseDto loginFallback(LoginDto loginDto, Exception ex) {
        log.error("Circuit breaker activated for user login: {}", ex.getMessage());
        throw new RuntimeException("Login service is temporarily unavailable");
    }
}
