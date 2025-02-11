package com.transaction.book.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String buildingNO;
    private String area;
    private String pincode;
    private String city;
    private String state;

    @OneToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;
}
