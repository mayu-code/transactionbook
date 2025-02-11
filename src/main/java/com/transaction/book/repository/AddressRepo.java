package com.transaction.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transaction.book.entities.Address;

public interface AddressRepo extends JpaRepository<Address,Long>{
    
}
