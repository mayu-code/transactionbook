package com.transaction.book.services.serviceInterface;

import com.transaction.book.entities.JwtToken;

public interface JwtTokenService {
    JwtToken addJwtToken(JwtToken jwtToken);
    JwtToken getTokenByToken(String token);
}
