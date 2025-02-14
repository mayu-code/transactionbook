package com.transaction.book.services.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.dto.responseDTO.CusotomerFullResponse;
import com.transaction.book.dto.responseDTO.CustomerResponse;
import com.transaction.book.dto.responseDTO.DueDate;
import com.transaction.book.entities.Customer;
import com.transaction.book.entities.Transaction;
import com.transaction.book.repository.CustomerRepo;
import com.transaction.book.repository.TransactionRepo;
import com.transaction.book.services.serviceInterface.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Override
    public Customer addCustomer(Customer customer) {
        return this.customerRepo.save(customer);
    }

    @Override
    public Customer getCustomerByMobileNo(String mobileNO) {
        return this.customerRepo.findByMobileNo(mobileNO);
    }

    @Override
    public Customer getCustomerById(long id) {
        return this.customerRepo.findById(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return this.customerRepo.findAllCustomers();
    }

    @Override
    public void deleteCusotmer(long id) {
        Customer customer = this.customerRepo.findById(id);
        if(customer.getTransactions()!=null){
            for(Transaction transaction:customer.getTransactions()){
                transaction.setDeleteFlag(true);
                this.transactionRepo.save(transaction);  
            }
            System.out.println("ok");
        }
        System.out.println("ok");
        customer.setDeleteFlag(true);
        this.customerRepo.save(customer);
        return;
    }

    @Override
    public double getTotalGetAmount() {
        return this.customerRepo.getTotalGetAmount();
    }

    @Override
    public double getToalGaveAmount() {
        return this.customerRepo.getTotalGaveAmount();
    }

    @Override
    public CusotomerFullResponse getCustomerResponseById(long id) {
        return this.customerRepo.findCustomerResponseById(id);
    }

    @Override
    public List<CustomerResponse> findAllCustomerResponse(String query,boolean gave,boolean get,boolean settel) {
        return this.customerRepo.findAllCustomerResponse(query,gave,get,settel);
    }

    @Override
    public DueDate getDueDateCustomer() {
        DueDate dueDate = new DueDate();
        System.out.println(LocalDate.now());
        dueDate.setTodaysDueDate(this.customerRepo.findTodaysDueDateCusotmers(String .valueOf(LocalDate.now())));
        dueDate.setTomorrowDueDate(this.customerRepo.findTodaysDueDateCusotmers(String.valueOf(LocalDate.now().plusDays(1))));
        return dueDate;
    }

    @Override
    public CusotomerFullResponse getCustomerResponseByName(String name) {
        return this.customerRepo.findCustomerResponseByName(name);
    }

}
