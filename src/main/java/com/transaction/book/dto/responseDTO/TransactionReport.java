package com.transaction.book.dto.responseDTO;

import lombok.Data;

@Data
public class TransactionReport {
    private double youGot;
    private double youGave;
    private double netBalance;
}
