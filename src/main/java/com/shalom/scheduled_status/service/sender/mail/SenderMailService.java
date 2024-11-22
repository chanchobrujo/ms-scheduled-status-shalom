package com.shalom.scheduled_status.service.sender.mail;

import com.shalom.scheduled_status.service.sender.ISenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("SenderMailService")
public class SenderMailService implements ISenderService {
    @Autowired
    private JavaMailSender sender;

    @Override
    public void send(Map<String, String> messageBody) {
        if (!messageBody.isEmpty()) {
            var text = messageBody.get("text");
            var email = messageBody.get("email");
            var subject = messageBody.get("subject");
            MimeMessage message = this.sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            try {
                helper.setTo(email);
                helper.setText(text, true);
                helper.setSubject(subject);
                this.sender.send(message);
            } catch (MessagingException e) {
                log.error("=================================");
                log.error("ERROR AL ENVIAR MENSAJE DE TEXTO");
                log.error(e.getMessage());
                log.error(messageBody.toString());
                log.error("=================================");
            }
        }
    }
}
