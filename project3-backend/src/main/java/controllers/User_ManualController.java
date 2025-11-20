package controllers;

import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/manual")
@RequiredArgsConstructor
public class User_ManualController {
    private final UserRepository userRepository;
    @PostMapping("/register")
    public User register(@RequestBody Registerreq req) {
        if(userRepository.existsByEmail(req.email)){
            return null;
        }
        User newUser = new User();
        newUser.setEmail(req.email);
        newUser.setDisplayName(req.displayName);
        newUser.setPasswordHash(req.password);
        newUser.setProvider(OAuthProvider.LOCAL);
        newUser.setProviderId(null);

        return userRepository.save(newUser);
    }
    @PostMapping("/login")
    public User login(@RequestBody Loginreq req){
        User user = userRepository.findByEmail(req.email).orElse(null);
        if(user == null){
            return null;
        }
        if(!user.getPasswordHash().equals(req.password)){
            return null;
        }
        return user;

    }
    public static class Registerreq {
        private String displayName;
        private String password;
        private String email;

    }
    public static class Loginreq {
        private String password;
        private String email;

    }

}
