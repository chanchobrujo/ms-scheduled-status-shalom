package com.shalom.scheduled_status.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderMessageService implements ISenderMessageService {
    private final JavaMailSender sender;
    @Override
    public void sendMessage(String text, String subject, String email) {
        MimeMessage message = this.sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setText(text, true);
            helper.setSubject(subject);
            this.sender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
