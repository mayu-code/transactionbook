package com.transaction.book.services.serviceInterface;

public interface OtpService {
    String generateAndSendOtp(String email);
    boolean verifyOtp(String email, String otp);
    String generateOtp();
    boolean varifySession(String email,String otp);
    boolean resetPassword(String email, String otp, String newPassword);
}
