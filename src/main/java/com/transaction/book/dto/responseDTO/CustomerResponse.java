package com.transaction.book.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private long id;
    private String name;
    private String mobileNo;
    private String gstinNo;
    private double amount;
    private String dueDate;
    private String updateDate;
}
