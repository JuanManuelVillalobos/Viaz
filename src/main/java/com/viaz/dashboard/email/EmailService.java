package com.viaz.dashboard.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

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
        String subject = "Invitación a Viaz Logistics";
        String message = getInvitationMessage(confirmationUrl);
        sendEmail(to, subject, message, token);
    }

    public void sendPasswordResetEmail(String to) throws MessagingException {
        String token = generateToken(); // Generate UUID
        String confirmationUrl = baseFrontUrl + "/auth/set-password?token=" + token + "&email=" + to;
        logger.info("Sending password reset email to: {}", to);
        String subject = "Solicitud de restablecimiento de contraseña";
        String message = getPasswordResetMessage(to, confirmationUrl);
        sendEmail(to, subject, message, token);
    }


    public void sendPasswordExpiredEmail(String to) throws MessagingException {
        String token = generateToken(); // Generate UUID
        String confirmationUrl = baseFrontUrl + "/auth/set-password?token=" + token + "&email=" + to;
        String subject = "Tu password ha expirado";
        String message = getPasswordExpiredMessage(to, confirmationUrl);
        sendEmail(to, subject, message, token);
    }


    private void sendEmail(String to, String subject, String message, String token) throws MessagingException {
        logger.info("Sending email to: {}", to);


        userTokens.put(to, token); // Store the token associated with the user's email

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true); // Set to true to indicate that this is HTML
        helper.setFrom(emailAddress); // Set your from address

        mailSender.send(mimeMessage);
        logger.info("Email sent successfully.");
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
        // Crear el mensaje en texto plano
        String plainTextMessage = "Estimado usuario,\n\n" +
                "Hemos recibido una solicitud para restablecer la contraseña de su cuenta asociada a este correo electrónico: " + to + ".\n\n" +
                "Para restablecer su contraseña, por favor haga clic en el siguiente enlace:\n" +
                confirmationUrl + "\n\n" +
                "Este enlace expirará en 24 horas por razones de seguridad. Si usted no solicitó el restablecimiento de contraseña, ignore este correo y su contraseña permanecerá sin cambios.\n\n" +
                "Gracias,\nViaz Logistica";

        // Crear el mensaje HTML con mejor estilo
        String htmlMessage = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" + // Asegurar la codificación correcta de caracteres
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<div style='max-width: 600px; margin: 50px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); text-align: center;'>" +
                "<h1 style='color: #333;'>Solicitud de restablecimiento de contraseña</h1>" +
                "<p>Estimado usuario,</p>" +
                "<p>Hemos recibido una solicitud para restablecer la contraseña de su cuenta asociada a este correo electrónico: <strong>" + to + "</strong>.</p>" +
                "<p>Para restablecer su contraseña, por favor haga clic en el siguiente enlace:</p>" +
                "<a href=\"" + confirmationUrl + "\" " +
                "style='display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; text-align: center;'>" +
                "Restablecer contraseña" +
                "</a>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "Este enlace expirará pronto por razones de seguridad. Si usted no solicitó el restablecimiento de contraseña, ignore este correo y su contraseña permanecerá sin cambios." +
                "</p>" +
                "<div style='margin-top: 20px; color: #777;'>" +
                "<p>Gracias,<br>Viaz Logistica</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return getMessage(plainTextMessage, htmlMessage);
    }


    private String getInvitationMessage(String invitationUrl) {
        // Crear el mensaje en texto plano
        String plainTextMessage = "Has sido invitado a unirte a nuestro sitio privado. Por favor, haz clic en el siguiente enlace para aceptar tu invitación y configurar tu cuenta:\n" +
                invitationUrl + "\n\n" +
                "Este enlace expirará en 24 horas por razones de seguridad. Si no solicitaste esta invitación, por favor ignora este correo.\n\n" +
                "Gracias,\nViaz Logistics";

        // Crear el mensaje HTML con mejor estilo
        String htmlMessage = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" + // Asegurar la codificación correcta de caracteres
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<div style='max-width: 600px; margin: 50px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); text-align: center;'>" +
                "<h1 style='color: #333;'>¡Estás invitado!</h1>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "Has sido invitado a unirte a nuestro Viaz Dashboard. Por favor, haz clic en el siguiente enlace para aceptar tu invitación y configurar tu cuenta:" +
                "</p>" +
                "<a href=\"" + invitationUrl + "\" " +
                "style='display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; text-align: center;'>" +
                "Únete ahora" +
                "</a>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "Este enlace expirará pronto por razones de seguridad. Si no solicitaste esta invitación, por favor ignora este correo." +
                "</p>" +
                "<div style='margin-top: 20px; color: #777;'>" +
                "<p>Gracias,<br>Viaz Logistics</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        // Combinar los mensajes de texto plano y HTML
        return getMessage(plainTextMessage, htmlMessage);
    }

    private String getPasswordExpiredMessage(String to, String resetUrl) {
        // Crear el mensaje en texto plano
        String plainTextMessage = "Estimado usuario,\n\n" +
                "La contraseña asociada a su cuenta con este correo electrónico: " + to + " ha expirado.\n\n" +
                "Por razones de seguridad, debe actualizar su contraseña. Por favor, haga clic en el siguiente enlace para establecer una nueva contraseña:\n" +
                resetUrl + "\n\n" +
                "Este enlace expirará en 24 horas. Si usted no solicitó esta actualización, por favor contacte con soporte inmediatamente.\n\n" +
                "Gracias,\nViaz Logistica";

        // Crear el mensaje HTML con mejor estilo
        String htmlMessage = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<div style='max-width: 600px; margin: 50px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); text-align: center;'>" +
                "<h1 style='color: #333;'>Actualización de contraseña requerida</h1>" +
                "<p>Estimado usuario,</p>" +
                "<p>La contraseña asociada a su cuenta con el correo electrónico <strong>" + to + "</strong> ha expirado.</p>" +
                "<p>Para continuar utilizando su cuenta de manera segura, por favor haga clic en el siguiente enlace para actualizar su contraseña:</p>" +
                "<a href=\"" + resetUrl + "\" " +
                "style='display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; text-align: center;'>" +
                "Actualizar contraseña" +
                "</a>" +
                "<p style='color: #555; line-height: 1.6;'>" +
                "Este enlace expirará en 24 horas. Si usted no solicitó esta actualización, contacte con soporte inmediatamente." +
                "</p>" +
                "<div style='margin-top: 20px; color: #777;'>" +
                "<p>Gracias,<br>Viaz Logistica</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return getMessage(plainTextMessage, htmlMessage);
    }


    private String getMessage(String plainTextMessage, String htmlMessage) {
        return "<html><body>" +
                "<p style='display:none;'>" + plainTextMessage + "</p>" + // Include plain text for email clients that don't support HTML
                htmlMessage +
                "</body></html>";
    }


}

