package com.viaz.dashboard.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String emailAddress;
    private final String baseFrontUrl;
    @Getter
    private final Map<String, String> userTokens = new HashMap<>(); // Store tokens for users [email, Token]


    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String emailAddress, @Value("${frontend.url}") String baseFrontUrl) {
        this.mailSender = mailSender;
        this.emailAddress = emailAddress;
        this.baseFrontUrl = baseFrontUrl;
    }

    public void sendInvitationEmail(String to) throws MessagingException {
        System.out.println("Sending email to: " + to);
        String subject = "You're Invited!";

        String token = generateToken(); // Generate UUID
        userTokens.put(to, token); // Store the token associated with the user's email

        String confirmationUrl = baseFrontUrl + "/auth/set-password?token=" + token + "&email=" + to;
        String message = "You have been invited to join our private site. Please set your password using the following link: <a href=\"" + confirmationUrl + "\">Set Password</a>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true); // Set to true to indicate that this is HTML
        helper.setFrom(emailAddress); // Set your from address

        mailSender.send(mimeMessage);
        System.out.println("Email sent successfully.");
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[32]; // 256 bits
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    public String getToken(String email) {
        return userTokens.get(email);
    }

    public void removeToken(String email) {
        userTokens.remove(email);
    }
}
