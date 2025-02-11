package com.transaction.book.dto.requestDTO;

import lombok.Data;

@Data
public class AddressRequest {
    private String buildingNo;
    private String area;
    private String pincode;
    private String city;
    private String state;
}
