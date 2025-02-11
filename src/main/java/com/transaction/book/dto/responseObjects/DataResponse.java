package com.transaction.book.dto.responseObjects;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class DataResponse {
    private String message;
    private HttpStatus httpStatus;
    private int statusCode;
    private Object data;
}
