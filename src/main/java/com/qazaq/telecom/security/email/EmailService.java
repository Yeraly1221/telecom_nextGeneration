package com.qazaq.telecom.security.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static final String EMAIL_SUBJECT = "Confirm your email";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void send(String to, String email){
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setFrom(fromEmail);
            mailSender.send(mimeMessage);

            LOGGER.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to: {}", to, e);
            throw new IllegalStateException("Failed to send email to: " + to, e);
        }
    }
}
