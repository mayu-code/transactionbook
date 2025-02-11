package com.transaction.book.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String mobileNo;
    private String gstinNo;
    private double amount;
    private String dueDate;
    private String updateDate;
    private String reference;

    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToOne(mappedBy = "customer",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Address address;
}