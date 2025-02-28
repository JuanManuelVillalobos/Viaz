package com.viaz.dashboard.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String emailAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String emailAddress) {
        this.mailSender = mailSender;
        this.emailAddress = emailAddress;
    }

    public void sendInvitationEmail(String to) throws MessagingException {
        System.out.println("Sending email to: " + to);
        String subject = "You're Invited!";
        String confirmationUrl = "http://localhost:5173/set-password?token=" + generateToken();
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
        return UUID.randomUUID().toString();
    }
}
