package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.LoginRequest;
import com.atlascv.atlascvbackend.dto.LoginResponse;
import com.atlascv.atlascvbackend.dto.ResendCodeRequest;
import com.atlascv.atlascvbackend.dto.SignupRequest;
import com.atlascv.atlascvbackend.dto.VerifyCodeRequest;
import com.atlascv.atlascvbackend.entity.EmailVerificationCode;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.EmailVerificationCodeRepository;
import com.atlascv.atlascvbackend.repository.UserRepository;
import com.atlascv.atlascvbackend.util.VerificationCodeGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MailTemplateService mailTemplateService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       EmailVerificationCodeRepository codeRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       MailTemplateService mailTemplateService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.mailTemplateService = mailTemplateService;
        this.jwtService = jwtService;
    }

    @Transactional
    public String signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email zaten kayıtlı.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        userRepository.save(user);

        createAndSendVerificationCode(user);

        return "Kayıt başarılı. Aktivasyon kodu email adresine gönderildi.";
    }

    @Transactional
    public String verifyCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        EmailVerificationCode latestCode = codeRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("Doğrulama kodu bulunamadı."));

        if (Boolean.TRUE.equals(latestCode.getUsed())) {
            throw new RuntimeException("Bu kod daha önce kullanılmış.");
        }

        if (latestCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş.");
        }

        if (!request.getCode().equals(latestCode.getCode())) {
            throw new RuntimeException("Kod hatalı.");
        }

        latestCode.setUsed(true);
        codeRepository.save(latestCode);

        user.setEnabled(true);
        userRepository.save(user);

        return "Email başarıyla doğrulandı.";
    }

    @Transactional
    public String resendCode(ResendCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Bu hesap zaten doğrulanmış.");
        }

        EmailVerificationCode latest = codeRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .orElse(null);

        if (latest != null && latest.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Tekrar kod istemek için 60 saniye bekleyin.");
        }

        createAndSendVerificationCode(user);

        return "Yeni doğrulama kodu gönderildi.";
    }
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Lütfen önce email adresinizi doğrulayın.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email veya şifre hatalı.");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    private void createAndSendVerificationCode(User user) {
        invalidateOldCodes(user);

        String rawCode = VerificationCodeGenerator.generateCode();

        EmailVerificationCode code = new EmailVerificationCode();
        code.setUser(user);
        code.setCode(rawCode);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        code.setUsed(false);
        code.setCreatedAt(LocalDateTime.now());

        codeRepository.save(code);

        String html = mailTemplateService.buildVerificationMail(user.getFullName(), rawCode);
        emailService.sendHtmlMail(user.getEmail(), "AtlasCV Aktivasyon Kodunuz", html);
    }


    private void invalidateOldCodes(User user) {
        List<EmailVerificationCode> oldCodes = codeRepository.findByUserAndUsedFalse(user);

        for (EmailVerificationCode c : oldCodes) {
            c.setUsed(true);
        }

        codeRepository.saveAll(oldCodes);
    }
}