package com.atlascv.atlascvbackend.security;

import com.atlascv.atlascvbackend.dto.*;
import com.atlascv.atlascvbackend.entity.EmailVerificationCode;
import com.atlascv.atlascvbackend.entity.PasswordResetCode;
import com.atlascv.atlascvbackend.entity.User;
import com.atlascv.atlascvbackend.repository.EmailVerificationCodeRepository;
import com.atlascv.atlascvbackend.repository.PasswordResetCodeRepository;
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
    private final PasswordResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MailTemplateService mailTemplateService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       EmailVerificationCodeRepository codeRepository,
                       PasswordResetCodeRepository resetCodeRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       MailTemplateService mailTemplateService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.resetCodeRepository = resetCodeRepository;
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

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Bu email ile kayıtlı hesap bulunamadı."));

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Hesabınız henüz doğrulanmamış.");
        }

        PasswordResetCode latest = resetCodeRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .orElse(null);

        if (latest != null && latest.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Tekrar kod istemek için 60 saniye bekleyin.");
        }

        // Eski kodları geçersiz kıl
        List<PasswordResetCode> oldCodes = resetCodeRepository.findByUserAndUsedFalse(user);
        oldCodes.forEach(c -> c.setUsed(true));
        resetCodeRepository.saveAll(oldCodes);

        String rawCode = VerificationCodeGenerator.generateCode();

        PasswordResetCode code = new PasswordResetCode();
        code.setUser(user);
        code.setCode(rawCode);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        code.setUsed(false);
        code.setCreatedAt(LocalDateTime.now());
        resetCodeRepository.save(code);

        String html = mailTemplateService.buildPasswordResetMail(user.getFullName(), rawCode);
        emailService.sendHtmlMail(user.getEmail(), "AtlasCV Şifre Sıfırlama Kodunuz", html);

        return "Şifre sıfırlama kodu email adresinize gönderildi.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        PasswordResetCode latestCode = resetCodeRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("Şifre sıfırlama kodu bulunamadı."));

        if (Boolean.TRUE.equals(latestCode.getUsed())) {
            throw new RuntimeException("Bu kod daha önce kullanılmış.");
        }

        if (latestCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş. Lütfen yeni kod isteyin.");
        }

        if (!request.getCode().equals(latestCode.getCode())) {
            throw new RuntimeException("Kod hatalı.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("Yeni şifre en az 6 karakter olmalıdır.");
        }

        latestCode.setUsed(true);
        resetCodeRepository.save(latestCode);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Şifreniz başarıyla güncellendi.";
    }

    @Transactional
    public String changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mevcut şifre hatalı.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("Yeni şifre en az 6 karakter olmalıdır.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Şifreniz başarıyla güncellendi.";
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