package com.transaction.book.jwtSecurity;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConstants {
    
    public static final String JWT_HEADER = "Authorization";

    @Value("${secret.key}")
    private String apiKey;
    
    private static String API_KEY;

    @PostConstruct
    public void init() {
        API_KEY = apiKey;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
