package com.transaction.book.dto.updateDto;

import com.transaction.book.dto.requestDTO.AddressRequest;

import lombok.Data;

@Data
public class UpdateCustomer {
    private long id;
    private String name;
    private String mobileNo;
    private String gstinNo;
    private String reference;
    private AddressRequest address;
}
