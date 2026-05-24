package com.atlascv.atlascvbackend.security;

import org.springframework.stereotype.Service;

@Service
public class MailTemplateService {

    public String buildPasswordResetMail(String fullName, String code) {
        return """
            <!DOCTYPE html>
            <html lang="tr">
            <head>
                <meta charset="UTF-8">
                <title>AtlasCV Şifre Sıfırlama</title>
            </head>
            <body style="margin:0;padding:0;background:#f4f6fb;font-family:Arial,sans-serif;">
                <div style="max-width:600px;margin:40px auto;background:#ffffff;border-radius:18px;overflow:hidden;border:1px solid #e5e7eb;">

                    <div style="background:linear-gradient(135deg,#FF6B6B,#ff4949);padding:28px;text-align:center;">
                        <h1 style="margin:0;color:white;">AtlasCV</h1>
                        <p style="margin:8px 0 0;color:rgba(255,255,255,0.9);">Şifre Sıfırlama Kodu</p>
                    </div>

                    <div style="padding:32px;">
                        <p style="font-size:16px;color:#111827;">Merhaba %s,</p>

                        <p style="font-size:15px;color:#374151;line-height:1.7;">
                            Şifrenizi sıfırlamak için aşağıdaki kodu kullanın:
                        </p>

                        <div style="text-align:center;margin:28px 0;">
                            <div style="display:inline-block;background:#fff5f5;padding:18px 32px;border-radius:14px;
                                        font-size:32px;font-weight:bold;letter-spacing:8px;color:#FF6B6B;
                                        border:2px dashed #FF6B6B;">
                                %s
                            </div>
                        </div>

                        <p style="font-size:14px;color:#6b7280;">
                            Bu kod <strong>15 dakika</strong> geçerlidir.
                        </p>

                        <p style="font-size:14px;color:#6b7280;">
                            Eğer bu işlemi siz yapmadıysanız bu maili dikkate almayabilirsiniz. Şifreniz değiştirilmeyecektir.
                        </p>
                    </div>

                    <div style="padding:18px;text-align:center;background:#fafafa;color:#9ca3af;font-size:12px;">
                        AtlasCV • CV Analiz Platformu
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, code);
    }

    public String buildVerificationMail(String fullName, String code) {
        return """
            <!DOCTYPE html>
            <html lang="tr">
            <head>
                <meta charset="UTF-8">
                <title>AtlasCV Email Doğrulama</title>
            </head>
            <body style="margin:0;padding:0;background:#f4f6fb;font-family:Arial,sans-serif;">
                <div style="max-width:600px;margin:40px auto;background:#ffffff;border-radius:18px;overflow:hidden;border:1px solid #e5e7eb;">
                    
                    <div style="background:linear-gradient(135deg,#3940c1,#5861ff);padding:28px;text-align:center;">
                        <h1 style="margin:0;color:white;">AtlasCV</h1>
                        <p style="margin:8px 0 0;color:rgba(255,255,255,0.9);">Email Aktivasyon Kodu</p>
                    </div>

                    <div style="padding:32px;">
                        <p style="font-size:16px;color:#111827;">Merhaba %s,</p>

                        <p style="font-size:15px;color:#374151;line-height:1.7;">
                            AtlasCV hesabınızı doğrulamak için aşağıdaki aktivasyon kodunu kullanın:
                        </p>

                        <div style="text-align:center;margin:28px 0;">
                            <div style="display:inline-block;background:#f3f4f6;padding:18px 32px;border-radius:14px;
                                        font-size:32px;font-weight:bold;letter-spacing:8px;color:#111827;">
                                %s
                            </div>
                        </div>

                        <p style="font-size:14px;color:#6b7280;">
                            Bu kod <strong>5 dakika</strong> geçerlidir.
                        </p>

                        <p style="font-size:14px;color:#6b7280;">
                            Eğer bu işlemi siz yapmadıysanız bu maili dikkate almayabilirsiniz.
                        </p>
                    </div>

                    <div style="padding:18px;text-align:center;background:#fafafa;color:#9ca3af;font-size:12px;">
                        AtlasCV • CV Analiz Platformu
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, code);
    }

}