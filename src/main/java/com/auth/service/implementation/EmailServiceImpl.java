package com.auth.service.implementation;

import com.auth.exception.EmailSendException;
import com.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String email, String resetLink) {
        String subject = "Reset password for AUTHAPP";
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setFrom("steelseries125@gmail.com");
            helper.setSubject(subject);
            String body = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Password Reset Request</title>
                <style>
                    .container {
                        width: 100%%;
                        padding: 20px;
                        background-color: #f7f7f7;
                        font-family: Arial, sans-serif;
                    }
                    .content {
                        max-width: 600px;
                        margin: 0 auto;
                        background: #ffffff;
                        padding: 30px;
                        border-radius: 8px;
                        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    .btn {
                        display: inline-block;
                        padding: 12px 25px;
                        margin-top: 20px;
                        font-size: 16px;
                        color: white;
                        background-color: #4CAF50;
                        text-decoration: none;
                        border-radius: 5px;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #888888;
                    }
                </style>
            </head>
            <body>
            <div class="container">
                <div class="content">
                    <h2>Password Reset Request</h2>
                    <p>Hello,</p>
                    <p>We received a request to reset your password. Click the button below to reset it:</p>
                    <a href="%s" class="btn">Reset Password</a>
                    <p>If you did not request a password reset, you can safely ignore this email.</p>
                    <div class="footer">
                        &copy; 2025 YourCompanyName. All rights reserved.
                    </div>
                </div>
            </div>
            </body>
            </html>
            """.formatted(resetLink);
            helper.setText(body, true);
            javaMailSender.send(message);
            log.info("EMAIL_SUCCESS: E-mail sent to {} with subject {}", email, subject);
        } catch (MessagingException e) {
            log.error("EMAIL_ERROR: Failed to send e-mail to {} with subject {}", email, subject);
            throw new EmailSendException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send e-mail");
        }
    }

}
