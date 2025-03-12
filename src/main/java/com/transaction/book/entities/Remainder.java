package com.transaction.book.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transaction.book.constants.RemainderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Remainder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String dueDate;
    private String reason;
    private boolean isDelete;
    private String addedDate;
    private double amount;
    private RemainderStatus status=RemainderStatus.Upcomming;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
