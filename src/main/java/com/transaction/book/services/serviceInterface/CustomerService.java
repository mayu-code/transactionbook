package com.transaction.book.services.serviceInterface;

import java.util.List;

import com.transaction.book.dto.responseDTO.CusotomerFullResponse;
import com.transaction.book.dto.responseDTO.CustomerResponse;
import com.transaction.book.dto.responseDTO.DueDate;
import com.transaction.book.entities.Customer;

public interface CustomerService {
    Customer addCustomer(Customer customer);
    Customer getCustomerByMobileNo(String mobileNO);
    Customer getCustomerById(long id);
    List<Customer> getAllCustomers();
    void deleteCusotmer(long id);
    double getTotalGetAmount();
    double getToalGaveAmount();

    CusotomerFullResponse getCustomerResponseById(long id);
    CusotomerFullResponse getCustomerResponseByName(String name);
    List<CustomerResponse> findAllCustomerResponse(String query,boolean gave,boolean get,boolean settel);

    DueDate getDueDateCustomer();
}
