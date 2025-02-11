package com.transaction.book.services.serviceInterface;

public interface EmailService {

    void sendMail();
    void sendOTP(String email,String otp);
    void sendApprovalEmail(String email);
}