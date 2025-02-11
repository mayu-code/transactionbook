package com.transaction.book.services.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.Address;
import com.transaction.book.repository.AddressRepo;
import com.transaction.book.services.serviceInterface.AddressService;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepo addressRepo;

    @Override
    public Address addAddress(Address address) {
        return this.addressRepo.save(address);
    }

    @Override
    public void deleteAddress(long id) {
        this.addressRepo.deleteById(id);
        return;
    }
    
}
