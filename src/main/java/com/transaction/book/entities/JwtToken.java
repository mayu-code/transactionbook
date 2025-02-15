package com.transaction.book.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transaction.book.constants.ClientType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String issuedAt;

    @Column(nullable = false)
    private Long expiration;

    private String logoutAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING) 
    private ClientType clientType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
