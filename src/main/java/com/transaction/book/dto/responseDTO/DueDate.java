package com.transaction.book.dto.responseDTO;

import java.util.List;

import lombok.Data;

@Data
public class DueDate {
    List<CustomerResponse> todaysDueDate;
    List<CustomerResponse> tomorrowDueDate;
    List<CustomerResponse> notPaymentYet;
}
