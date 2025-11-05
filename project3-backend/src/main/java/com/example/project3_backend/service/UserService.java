package com.example.project3_backend.service;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /** Create or update a user from OAuth login details. */
    public User upsertOAuthUser(String email,
                                String displayName,
                                String avatarUrl,
                                OAuthProvider provider,
                                String providerId) {

        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(u -> {
                    u.setEmail(email);
                    u.setDisplayName(displayName);
                    u.setAvatarUrl(avatarUrl);
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .displayName(displayName)
                                .avatarUrl(avatarUrl)
                                .provider(provider)
                                .providerId(providerId)
                                .build()
                ));
    }
}