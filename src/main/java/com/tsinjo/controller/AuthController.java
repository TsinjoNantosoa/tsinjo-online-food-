package com.tsinjo.controller;

import com.tsinjo.config.JwtProvider;
import com.tsinjo.exception.DuplicateResourceException;
import com.tsinjo.model.Cart;
import com.tsinjo.model.USER_ROLE;
import com.tsinjo.model.User;
import com.tsinjo.repository.CartRepository;
import com.tsinjo.repository.UserRepository;
import com.tsinjo.request.LoginRequest;
import com.tsinjo.request.SignupRequest;
import com.tsinjo.response.AuthResponse;
import com.tsinjo.response.UserResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/auth")
@SecurityRequirements
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CartRepository cartRepository;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider, AuthenticationManager authenticationManager,
                          CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmail(email) != null) {
            throw new DuplicateResourceException("An account already exists for this email");
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(request.getFullName().trim());
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cart.setTotal(0L);
        cartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null,
                List.of(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.name())));
        log.info("Customer account created for {}", email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response(jwtProvider.generateToken(authentication), "Registration successful", savedUser));
    }

    @PostMapping({"/signin", "/signing"})
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (AuthenticationException exception) {
            log.warn("Failed login attempt for {}", email);
            throw exception;
        }
        User user = userRepository.findByEmail(email);
        log.info("Successful login for {}", email);
        return ResponseEntity.ok(response(jwtProvider.generateToken(authentication), "Login successful", user));
    }

    private AuthResponse response(String jwt, String message, User user) {
        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setMessage(message);
        response.setUser(new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole(), java.util.List.copyOf(user.getAddresses())));
        return response;
    }
}
