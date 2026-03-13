package com.atlascv.atlascvbackend.controller;

import com.atlascv.atlascvbackend.dto.LoginRequest;
import com.atlascv.atlascvbackend.dto.LoginResponse;
import com.atlascv.atlascvbackend.dto.ResendCodeRequest;
import com.atlascv.atlascvbackend.dto.SignupRequest;
import com.atlascv.atlascvbackend.dto.VerifyCodeRequest;
import com.atlascv.atlascvbackend.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        String message = authService.signup(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyCodeRequest request) {
        String message = authService.verifyCode(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody ResendCodeRequest request) {
        String message = authService.resendCode(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}