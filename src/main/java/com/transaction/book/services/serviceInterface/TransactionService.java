package com.transaction.book.services.serviceInterface;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.transaction.book.dto.responseDTO.TransactionResponse;
import com.transaction.book.entities.Transaction;

public interface TransactionService {
    Transaction addTransaction(Transaction transaction);
    void deleteTransaction(long id);
    Transaction getTransactionById(long id);

    List<Transaction> getTrasactionsByCustomerId(long id);

    List<Transaction> getAfterTransactions(long id,String date);

    Transaction findPreviousTransaction(long id,String date);

    List<TransactionResponse> getAllTrasactions();

}
