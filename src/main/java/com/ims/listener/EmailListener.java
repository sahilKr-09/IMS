package com.ims.listener;

import com.ims.dto.EmailMessage;
import com.ims.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    @Autowired
    private EmailService emailService;

    @JmsListener(destination = "email-queue")
    public void receiveMessage(EmailMessage emailMessage) {
        // Send email
        emailService.sendEmail(emailMessage);
    }
}
