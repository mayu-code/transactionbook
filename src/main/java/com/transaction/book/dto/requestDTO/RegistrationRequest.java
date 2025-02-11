package com.transaction.book.dto.requestDTO;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String name;
    private String mobileNo;
    private String password;
    private String confirmPassword;
    private String email;
}
