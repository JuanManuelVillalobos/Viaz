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
        String token = generateToken(); // Generate UUID
        String confirmationUrl = baseFrontUrl + "/auth/set-password?token=" + token + "&email=" + to;
        String subject = "You're Invited to Access ViazDashboard";
        String message = getInvitationMessage(confirmationUrl);
        sendEmail(to, subject, message, token);
    }

    public void sendPasswordResetEmail(String to) throws MessagingException {
        String token = generateToken(); // Generate UUID
        String confirmationUrl = baseFrontUrl + "/auth/set-password?token=" + token + "&email=" + to;
        String subject = "Password Reset Request";
        String message = getPasswordResetMessage(to, confirmationUrl);
        sendEmail(to, subject, message, token);
    }

    private void sendEmail(String to, String subject, String message, String token) throws MessagingException {
        System.out.println("Sending email to: " + to);


        userTokens.put(to, token); // Store the token associated with the user's email

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

    private String getPasswordResetMessage(String to, String confirmationUrl) {
        // Create the plain text message
        String plainTextMessage = "Dear User,\n\n" +
                "We received a request to reset the password for your account associated with this email address: " + to + ".\n\n" +
                "To reset your password, please click the link below:\n" +
                confirmationUrl + "\n\n" +
                "This link will expire in 24 hours for security reasons. If you did not request a password reset, please ignore this email, and your password will remain unchanged.\n\n" +
                "Thank you,\nYour Company Name";

        // Create the HTML message with improved styling
        String htmlMessage = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" + // Ensure proper character encoding
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<div style='max-width: 600px; margin: 50px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); text-align: center;'>" +
                "<h1 style='color: #333;'>Password Reset Request</h1>" +
                "<p>Dear User,</p>" +
                "<p>We received a request to reset the password for your account associated with this email address: <strong>" + to + "</strong>.</p>" +
                "<p>To reset your password, please click the link below:</p>" +
                "<a href=\"" + confirmationUrl + "\" " +
                "style='display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; text-align: center;'>" +
                "Reset Password" +
                "</a>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "This link will expire soon for security reasons. If you did not request a password reset, please ignore this email, and your password will remain unchanged." +
                "</p>" +
                "<div style='margin-top: 20px; color: #777;'>" +
                "<p>Thank you,<br>Viaz Logistica</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

         return getMessage(plainTextMessage, htmlMessage);
    }

    private String getInvitationMessage(String invitationUrl) {
        // Create the plain text message
        String plainTextMessage = "You have been invited to join our private site. Please click the link below to accept your invitation and set up your account:\n" +
                invitationUrl + "\n\n" +
                "This link will expire in 24 hours for security reasons. If you did not request this invitation, please ignore this email.\n\n" +
                "Thank you,\nYour Company Name";

        // Create the HTML message with improved styling
        String htmlMessage = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" + // Ensure proper character encoding
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<div style='max-width: 600px; margin: 50px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); text-align: center;'>" +
                "<h1 style='color: #333;'>You're Invited!</h1>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "You have been invited to join our Viaz Dashboard. Please click the link below to accept your invitation and set up your account:" +
                "</p>" +
                "<a href=\"" + invitationUrl + "\" " +
                "style='display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; text-align: center;'>" +
                "Join Now" +
                "</a>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "This link will expire soon for security reasons. If you did not request this invitation, please ignore this email." +
                "</p>" +
                "<div style='margin-top: 20px; color: #777;'>" +
                "<p>Thank you,<br>Viaz Logistics</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        // Combine the plain text and HTML messages
        return getMessage(plainTextMessage, htmlMessage);
    }

    private String getMessage(String plainTextMessage, String htmlMessage) {
        return "<html><body>" +
                "<p style='display:none;'>" + plainTextMessage + "</p>" + // Include plain text for email clients that don't support HTML
                htmlMessage +
                "</body></html>";
    }
}

