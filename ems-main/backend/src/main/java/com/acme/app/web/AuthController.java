package com.acme.app.web;

import com.acme.app.domain.User;
import com.acme.app.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
    public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public static class RegisterRequest {
        @NotBlank
        public String name;
        @NotBlank
        @Email
        public String email;
        @NotBlank
        public String password;
    }

    public static class LoginRequest {
        @NotBlank
        @Email
        public String email;
        @NotBlank
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User created = userService.register(req.name, req.email, req.password);
            return ResponseEntity.ok(Map.of("id", created.getId(), "name", created.getName(), "email", created.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) { // race-condition safety
            return ResponseEntity.status(409).body(Map.of("error", "Email already registered"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userService.authenticate(req.email, req.password)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of("id", u.getId(), "name", u.getName(), "email", u.getEmail())))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}


