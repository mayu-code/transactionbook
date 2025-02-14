package com.transaction.book.dto.updateDto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UpdateTransaction {

    @Min(value = 1, message = "Transaction ID must be greater than 0")
    private Long id;

    private Double amount;

    @NotBlank(message = "Date is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
        message = "Date must be in format yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String date;

    @Size(max = 255, message = "Detail must not exceed 255 characters")
    private String detail;

    private boolean gave;
    private boolean got;
}
