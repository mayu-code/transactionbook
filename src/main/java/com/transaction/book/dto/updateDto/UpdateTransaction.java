package com.transaction.book.dto.updateDto;

import lombok.Data;

@Data
public class UpdateTransaction {
    private long id;
    private double amount;
    private String date;
    private String detail;
    private boolean gave;
    private boolean got;
}
