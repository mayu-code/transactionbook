package com.transaction.book.dto.requestDTO;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class DueDateRequest {

    @Min(value = 1, message = "ID must be greater than 0")
    private Long id;

    @NotBlank(message = "Due date is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Due date must be in format yyyy-MM-dd"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String dueDate;

    private double amount;

    private String reason;
}
