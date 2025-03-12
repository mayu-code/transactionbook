package com.transaction.book.services.logicService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.constants.RemainderStatus;
import com.transaction.book.dto.requestDTO.NewTransactionRequest;
import com.transaction.book.dto.updateDto.UpdateTransaction;
import com.transaction.book.entities.Customer;
import com.transaction.book.entities.Remainder;
import com.transaction.book.entities.Transaction;
import com.transaction.book.helper.DateTimeFormat;
import com.transaction.book.services.serviceImpl.CustomerServiceImpl;
import com.transaction.book.services.serviceImpl.RemainderServiceImpl;
import com.transaction.book.services.serviceImpl.TransactionServiceImpl;

@Service
public class TransactionMethods {

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private RemainderServiceImpl remainderServiceImpl;

    public boolean addNewTransaction(NewTransactionRequest request,byte[] bill){
        Customer customer = this.customerServiceImpl.getCustomerById(request.getCustomerId());
        Transaction transaction = new Transaction();
            transaction.setDate(request.getDate());
            transaction.setDetail(request.getDetail());
            transaction.setBill(bill);

            if(request.isGave()){
                customer.setAmount(customer.getAmount()+(request.getAmount()*(-1)));
                transaction.setAmount(request.getAmount()*(-1));
            }
            else{
                customer.setAmount(customer.getAmount()+request.getAmount());
                transaction.setAmount(request.getAmount());


                Remainder remainder = this.remainderServiceImpl.getExactLastRemainder(customer.getId());
                if(remainder!=null && remainder.getStatus().equals(RemainderStatus.Upcoming)){
                    remainder.setStatus(RemainderStatus.Successful);
                    remainder.setAmount(request.getAmount());
                    this.remainderServiceImpl.addRemainder(remainder);
                    customer.setDueDate(null);
                }
            }

            Transaction transaction3 = this.transactionServiceImpl.findPreviousTransaction(customer.getId(), request.getDate());
            if(transaction3==null){
                transaction.setBalanceAmount(transaction.getAmount());
            }else{
                transaction.setBalanceAmount(transaction3.getBalanceAmount()+transaction.getAmount());
            }

            

            customer.setUpdateDate(DateTimeFormat.format(LocalDateTime.now()));
            customer= this.customerServiceImpl.addCustomer(customer);

            transaction.setCustomer(customer);
            transaction = this.transactionServiceImpl.addTransaction(transaction);

            double balance = transaction.getBalanceAmount();
            for(Transaction transaction2:this.transactionServiceImpl.getAfterTransactions(customer.getId(), request.getDate())){
                transaction2.setBalanceAmount(balance+transaction2.getAmount());
                transaction2= this.transactionServiceImpl.addTransaction(transaction2);
                balance = transaction2.getBalanceAmount();
            }
            return true;
    }

    public boolean updateTransaction(long id,UpdateTransaction request,byte[] bill){
        Customer customer = this.customerServiceImpl.getCustomerById(id);
        Transaction transaction1 = this.transactionServiceImpl.getTransactionById(request.getId());
        customer.setAmount(customer.getAmount()-transaction1.getAmount());
        transaction1.setDate(null);
        Transaction transaction = new Transaction();
        transaction = this.transactionServiceImpl.addTransaction(transaction1);
            transaction.setDate(request.getDate());
            transaction.setDetail(request.getDetail());

            if(request.isGave()){
                customer.setAmount(customer.getAmount()+(request.getAmount()*(-1)));
                transaction.setAmount(request.getAmount()*(-1));
            }
            else{
                customer.setAmount(customer.getAmount()+request.getAmount());
                transaction.setAmount(request.getAmount());
            }

            Transaction transaction3 = this.transactionServiceImpl.findPreviousTransaction(customer.getId(), request.getDate());
            if(transaction3==null){
                transaction.setBalanceAmount(transaction.getAmount());
            }else{
                transaction.setBalanceAmount(transaction3.getBalanceAmount()+transaction.getAmount());
            }

            

            customer.setUpdateDate(DateTimeFormat.format(LocalDateTime.now()));
            customer= this.customerServiceImpl.addCustomer(customer);

            transaction.setCustomer(customer);
            transaction.setBill(bill);
            transaction = this.transactionServiceImpl.addTransaction(transaction);

            double balance = transaction.getBalanceAmount();
            for(Transaction transaction2:this.transactionServiceImpl.getAfterTransactions(customer.getId(), request.getDate())){
                transaction2.setBalanceAmount(balance+transaction2.getAmount());
                transaction2= this.transactionServiceImpl.addTransaction(transaction2);
                balance = transaction2.getBalanceAmount();
            }
            return true;
    }
}
