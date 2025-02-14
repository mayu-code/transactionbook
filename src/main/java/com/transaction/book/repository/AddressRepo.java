package com.transaction.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.transaction.book.entities.Address;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long>{
    
}
