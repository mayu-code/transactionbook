package com.transaction.book.services.serviceInterface;

import com.transaction.book.entities.Address;

public interface AddressService {
    Address addAddress(Address address);
    void deleteAddress(long id);
}
