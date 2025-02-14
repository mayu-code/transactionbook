package com.transaction.book.jwtSecurity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;

import com.transaction.book.constants.ClientType;
import com.transaction.book.entities.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

    public static SecretKey key = Keys.hmacShaKeyFor(JwtConstants.API_KEY.getBytes());

    public static JwtToken generateJwt(Authentication authentication,ClientType clientType){
        JwtToken jwtToken = new JwtToken();
        Long expirationTime=0L;
        if(clientType.equals(ClientType.WEB)){
            expirationTime = 24*60*60*1000L;
        }
        else if(clientType.equals(ClientType.MOBILE)){
            expirationTime = 90*24*60*60*1000L;
        }
        else{
            expirationTime = 60*60*1000L;
        }

        String jwt = Jwts.builder()
                    .setIssuer("ok").setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime()+84600000))
                    .claim("email", authentication.getName())
                    .claim("role", authentication.getAuthorities().toArray()[0].toString())
                    .signWith(key)
                    .compact();
        jwtToken.setToken(jwt);
        jwtToken.setExpiration(expirationTime);
            
        return jwtToken;
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
