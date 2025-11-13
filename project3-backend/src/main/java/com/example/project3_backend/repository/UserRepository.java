package com.example.project3_backend.repository;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(OAuthProvider provider, String providerId);
    boolean existsByEmail(String email);
    List<User> findByDisplayNameContainingIgnoreCase(String q);
}