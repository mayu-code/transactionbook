package com.transaction.book.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private long id;
    private double amount;
    private double balanceAmount;
    private String date;
    private String detail;
    private String customerName;
}
