package com.example.project3_backend.controllers;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import com.example.project3_backend.service.UserService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @NonNull RegisterReq req) {
        if(userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }
        User user = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .avatarUrl(req.getAvatarUrl())
                .passwordHash(bCryptPasswordEncoder.encode(req.getPassword()))
                .provider(OAuthProvider.LOCAL)
                .build();
        return ResponseEntity.ok(userRepository.save(user));
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @NonNull LoginReq req) {
        User user  = userRepository.findByEmail(req.getEmail()).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");

        }
        boolean match = bCryptPasswordEncoder.matches(req.getPassword(), user.getPasswordHash());
        if(!match) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");

        }
        return ResponseEntity.ok(user);

    }
    @Getter
    @Setter
    public static class RegisterReq{
        private String username;
        private String password;
        private String email;
        private String avatarUrl;
    }
    @Getter
    @Setter
    public static class LoginReq{
        private String email;
        private String password;
    }
}
