package com.atlascv.atlascvbackend.business;

import com.atlascv.atlascvbackend.dto.AuthResponse;
import com.atlascv.atlascvbackend.dto.LoginRequest;
import com.atlascv.atlascvbackend.dto.SignupRequest;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.UserRepository;
import com.atlascv.atlascvbackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthManager(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Bu email zaten kayıtlı.");
        }

        User u = new User();
        u.setFullName(request.getFullName().trim());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(request.getPassword())); // HASH

        userRepository.save(u);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifre hatalı.");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail());
    }
}
