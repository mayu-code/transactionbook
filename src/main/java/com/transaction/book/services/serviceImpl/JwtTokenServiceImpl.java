package com.transaction.book.services.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.JwtToken;
import com.transaction.book.repository.JwtTokenRepo;
import com.transaction.book.services.serviceInterface.JwtTokenService;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Autowired
    private JwtTokenRepo jwtTokenRepo;

    @Override
    public JwtToken addJwtToken(JwtToken jwtToken) {
        return this.jwtTokenRepo.save(jwtToken);
    }

    @Override
    public JwtToken getTokenByToken(String token) {
        return this.jwtTokenRepo.findbyToken(token);
    }
    
}
