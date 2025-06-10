package com.ims.service;

import com.ims.dto.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.getTo());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getBody());
        mailSender.send(message);
    }
}

