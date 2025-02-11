package com.transaction.book.jwtSecurity;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {
    public static SecretKey key = Keys.hmacShaKeyFor(JwtConstants.API_KEY.getBytes());

    public static String generateJwt(Authentication authentication){
        String jwt = Jwts.builder()
                    .setIssuer("ok").setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime()+84600000))
                    .claim("email", authentication.getName())
                    .claim("role", authentication.getAuthorities().toArray()[0].toString())
                    .signWith(key)
                    .compact();
        return jwt;         
    }

    public static String getEmailByJwt(String jwt){
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parser()
                        .setSigningKey(key).build()
                        .parseClaimsJws(jwt).getBody();

        String mobileNo = String.valueOf(claims.get("email"));
        return mobileNo;
    }

    public static String getRoleByJwt(String jwt){
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parser()
                        .setSigningKey(key).build()
                        .parseClaimsJws(jwt).getBody();

        String mobileNo = String.valueOf(claims.get("role"));
        return mobileNo;
    }
}
