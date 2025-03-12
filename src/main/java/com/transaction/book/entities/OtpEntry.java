package com.transaction.book.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class OtpEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime expirationTime;
    private LocalDateTime sessionTime;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    public boolean isSessionExpired() {
        return LocalDateTime.now().isAfter(sessionTime);
    }
}
