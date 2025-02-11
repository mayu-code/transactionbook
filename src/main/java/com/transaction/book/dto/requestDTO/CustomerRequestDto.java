package com.transaction.book.dto.requestDTO;

import lombok.Data;

@Data
public class CustomerRequestDto {
    private String name;
    private String mobileNo;
    private String gstinNo;
    private String reference;
    private double amount;
    private String date;
    private boolean gave;
    private boolean got;
    private AddressRequest address;
}
