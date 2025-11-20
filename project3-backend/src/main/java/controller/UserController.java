package com.example.project3_backend.controller;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable @NonNull UUID id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable @NonNull String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @NonNull UserRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + request.getEmail() + " already exists");
        }

        // Create User entity
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .avatarUrl(request.getAvatarUrl())
                .provider(request.getProvider() != null ? request.getProvider() : OAuthProvider.GOOGLE)
                .providerId(request.getProviderId())
                .build();

        User savedUser = Objects.requireNonNull(userRepository.save(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @NonNull UUID id, @RequestBody UserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                        // Check if new email already exists
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new IllegalArgumentException("Email already exists");
                        }
                        user.setEmail(request.getEmail());
                    }
                    if (request.getUsername() != null) {
                        user.setUsername(request.getUsername());
                    }
                    if (request.getDisplayName() != null) {
                        user.setDisplayName(request.getDisplayName());
                    }
                    if (request.getAvatarUrl() != null) {
                        user.setAvatarUrl(request.getAvatarUrl());
                    }
                    if (request.getProvider() != null) {
                        user.setProvider(request.getProvider());
                    }
                    if (request.getProviderId() != null) {
                        user.setProviderId(request.getProviderId());
                    }
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NonNull UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO for User creation/update request
    public static class UserRequest {
        private String email;
        private String username;
        private String displayName;
        private String avatarUrl;
        private OAuthProvider provider;
        private String providerId;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

        public OAuthProvider getProvider() { return provider; }
        public void setProvider(OAuthProvider provider) { this.provider = provider; }

        public String getProviderId() { return providerId; }
        public void setProviderId(String providerId) { this.providerId = providerId; }
    }
}