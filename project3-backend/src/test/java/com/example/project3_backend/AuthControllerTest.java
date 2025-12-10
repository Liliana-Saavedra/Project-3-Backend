package com.example.project3_backend;

import com.example.project3_backend.controllers.AuthController;
import com.example.project3_backend.model.User;
import com.example.project3_backend.model.enums.OAuthProvider;
import com.example.project3_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


//https://www.baeldung.com/spring-boot-testing

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest{
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //register sucess
    @Test
    void registerUserTest() throws Exception {
        AuthController.RegisterReq req = new AuthController.RegisterReq();
        req.setEmail("test@gmail.com");
        req.setPassword("password");
        req.setUsername("test");
        req.setAvatarUrl("image.png");

        Mockito.when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        Mockito.when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .username("test").avatarUrl("image.png").email("test@gmail.com").passwordHash("hashedPassword").provider(OAuthProvider.LOCAL).build();
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(savedUser);


        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.email").value("test@gmail.com"))
                        .andExpect(jsonPath("$.username").value("test"));
    }
    }

