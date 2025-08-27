package com.bosco.stdata.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {


    private final JavaMailSender mailSender;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    
    public void sendSimpleMessage(String to, String subject, String text) {
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        Boolean isMultipart = false;

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");

            //SimpleMailMessage message = new SimpleMailMessage(); 
            messageHelper.setFrom("noreply@boscok12.com");
            messageHelper.setTo(to); 
            messageHelper.setSubject(subject); 
            messageHelper.setText(text, true);
            mailSender.send(mimeMessage);

            System.out.println("Sent email");
        }
        catch (Exception ex) {
            System.out.println("Failed To send Email");
        }
        
    }
}
