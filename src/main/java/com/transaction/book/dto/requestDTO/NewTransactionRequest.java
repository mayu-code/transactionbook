package com.transaction.book.dto.requestDTO;

import lombok.Data;

@Data
public class NewTransactionRequest {
    private long customerId;
    private double amount;
    private String date;
    private String detail;
    private boolean gave;
    private boolean got;
}
