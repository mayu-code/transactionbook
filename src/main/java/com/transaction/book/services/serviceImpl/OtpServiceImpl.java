package com.transaction.book.services.serviceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.OtpEntry;
import com.transaction.book.entities.User;
import com.transaction.book.repository.OtpRepo;
import com.transaction.book.services.serviceInterface.OtpService;


@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private OtpRepo otpRepo;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    @Override
    public String generateAndSendOtp(String email) {

        otpRepo.deleteByEmail(email);
        String otp = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2);
        LocalDateTime sessionTime = LocalDateTime.now().plusMinutes(5);

        OtpEntry otpEntry = new OtpEntry();
        otpEntry.setEmail(email);
        otpEntry.setOtp(otp);
        otpEntry.setExpirationTime(expirationTime);
        otpEntry.setSessionTime(sessionTime);
        otpRepo.save(otpEntry);

        this.emailServiceImpl.sendOTP(email, otp);
        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpEntry otpEntry = otpRepo.findByEmail(email).orElse(null);
        return otpEntry != null && !otpEntry.isExpired() && otpEntry.getOtp().equals(otp);
    }

    @Override
    public boolean varifySession(String email,String otp){
        OtpEntry otpEntry = otpRepo.findByEmail(email).orElse(null);
        return otpEntry != null && !otpEntry.isSessionExpired() && otpEntry.getOtp().equals(otp);
    }

    @Override
    public String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) {
        if (!varifySession(email, otp)) {
            return false;
        }

        User user = this.userServiceImpl.getUserByEmail(email);

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        this.userServiceImpl.registerUser(user);
        otpRepo.deleteByEmail(email);
        return true;
    }
}
