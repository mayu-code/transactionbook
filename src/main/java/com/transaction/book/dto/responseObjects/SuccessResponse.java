package com.transaction.book.dto.responseObjects;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class SuccessResponse {
    private String message;
    private HttpStatus httpStatus;
    private int statusCode;
}
