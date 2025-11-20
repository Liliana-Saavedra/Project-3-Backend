package controllers;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import com.example.project3_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/oauth")
    public ResponseEntity<User> loginWithOAuth(@RequestBody OAuthRequest req) {
        User savedUser = userService.upsertOAuthUser(
                req.email(),
                req.displayName(),
                req.avatarUrl(),
                req.provider(),
                req.providerId()

        );
        return ResponseEntity.ok(savedUser);
    }
    public record OAuthRequest(
            String email,
            String displayName,
            String avatarUrl,
            OAuthProvider provider,
            String providerId
    ){}
}
