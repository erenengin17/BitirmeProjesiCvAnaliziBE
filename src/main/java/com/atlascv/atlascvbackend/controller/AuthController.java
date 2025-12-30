package com.atlascv.atlascvbackend.controller;

import com.atlascv.atlascvbackend.business.AuthManager;
import com.atlascv.atlascvbackend.dto.AuthResponse;
import com.atlascv.atlascvbackend.dto.LoginRequest;
import com.atlascv.atlascvbackend.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthManager authManager;

    public AuthController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        authManager.signup(request);
        return ResponseEntity.ok("Kayıt başarılı");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authManager.login(request));
    }
}