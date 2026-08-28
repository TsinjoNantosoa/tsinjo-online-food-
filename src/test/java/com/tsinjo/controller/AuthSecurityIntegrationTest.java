package com.tsinjo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsinjo.model.USER_ROLE;
import com.tsinjo.model.User;
import com.tsinjo.repository.CartRepository;
import com.tsinjo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired CartRepository cartRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        cartRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void signupAlwaysCreatesCustomerEvenWhenAdminRoleIsSubmitted() throws Exception {
        MvcResult signup = mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Alice Doe","email":"alice@example.com",
                                 "password":"password123","role":"ROLE_ADMIN"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"))
                .andExpect(jsonPath("$.jwt").isNotEmpty())
                .andReturn();
        assertThat(userRepository.findByEmail("alice@example.com").getRole()).isEqualTo(USER_ROLE.ROLE_CUSTOMER);
        String jwt = objectMapper.readTree(signup.getResponse().getContentAsString()).get("jwt").asText();
        mockMvc.perform(get("/api/users/profile").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void signupHashesPasswordWithBcrypt() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Bob Doe\",\"email\":\"bob@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
        String hash = userRepository.findByEmail("bob@example.com").getPassword();
        assertThat(hash).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", hash)).isTrue();
    }

    @Test
    void signinReturnsJwtForValidCredentials() throws Exception {
        User user = new User();
        user.setFullName("Carla Doe");
        user.setEmail("carla@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        userRepository.save(user);

        mockMvc.perform(post("/auth/signin").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"carla@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
    }

    @Test
    void protectedEndpointRejectsMissingJwt() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void signupRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"invalid\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
