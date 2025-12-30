package com.atlascv.atlascvbackend.business;

import com.atlascv.atlascvbackend.dto.AuthResponse;
import com.atlascv.atlascvbackend.dto.LoginRequest;
import com.atlascv.atlascvbackend.dto.SignupRequest;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.UserRepository;
import com.atlascv.atlascvbackend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthManager {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthManager(
            UserRepository userRepository,
            BCryptPasswordEncoder encoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("Bu email zaten kayıtlı.");
        }

        User user = new User();
        user.setFullName(request.fullName);
        user.setEmail(request.email);
        user.setPasswordHash(encoder.encode(request.password));

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (!encoder.matches(request.password, user.getPasswordHash())) {
            throw new RuntimeException("Şifre hatalı.");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
