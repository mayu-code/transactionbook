package com.transaction.book.dto.updateDto;

import com.google.firebase.database.annotations.NotNull;
import com.transaction.book.dto.requestDTO.AddressRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomer {


    @Min(value = 1, message = "ID must be greater than 0")
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number format")
    private String mobileNo;

    @Pattern(regexp = "^[0-9A-Z]{15}$", message = "Invalid GSTIN format")
    private String gstinNo;

    private String reference;

    private AddressRequest address;
}
