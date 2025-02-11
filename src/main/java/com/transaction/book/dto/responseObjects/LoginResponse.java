package com.transaction.book.dto.responseObjects;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private HttpStatus httpStatus;
    private int statusCode;
    private String role;
    private String token;
}
