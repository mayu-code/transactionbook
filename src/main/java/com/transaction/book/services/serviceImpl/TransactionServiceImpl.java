package com.transaction.book.services.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.dto.responseDTO.TransactionResponse;
import com.transaction.book.entities.Transaction;
import com.transaction.book.repository.TransactionRepo;
import com.transaction.book.services.serviceInterface.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private TransactionRepo transactionRepo;

    @Override
    public Transaction addTransaction(Transaction transaction) {
        return this.transactionRepo.save(transaction);
    }

    @Override
    public void deleteTransaction(long id) {
        this.transactionRepo.deleteById(id);
        return;
    }

    @Override
    public Transaction getTransactionById(long id) {
        return this.transactionRepo.findById(id).get();
    }

    @Override
    public List<Transaction> getTrasactionsByCustomerId(long id) {
        return this.transactionRepo.findByCustomerId(id);
    }

    @Override
    public List<Transaction> getAfterTransactions(long customerId, String date) {
        return this.transactionRepo.findAfterTransactions(customerId, date);   
    }

    @Override
    public Transaction findPreviousTransaction(long id, String date) {
        return this.transactionRepo.findPreviousTransaction(id, date);
    }

    @Override
    public List<TransactionResponse> getAllTrasactions() {
        return this.transactionRepo.findAllTransactions();
    }
}
