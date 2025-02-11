package com.transaction.book.services.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.transaction.book.services.serviceInterface.EmailService;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMail() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMail'");
    }

    @Override
    public void sendOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Dear user,\n\nYour OTP code is: " + otp + "\n\nThis OTP is valid for 5 minutes.\n\nRegards,\nYour Company Name");

        javaMailSender.send(message);
    }

    @Override
    public void sendApprovalEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendApprovalEmail'");
    }
    
}
