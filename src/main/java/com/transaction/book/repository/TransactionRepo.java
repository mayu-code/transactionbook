package com.transaction.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.transaction.book.dto.responseDTO.TransactionResponse;
import com.transaction.book.entities.Transaction;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.customer.id=:id  ORDER BY t.date DESC")
    List<Transaction> findByCustomerId(@Param("id") long id);

    @Query("SELECT t FROM Transaction t WHERE t.customer.id=:customerId AND t.date>:date AND t.date IS NOT NULL ORDER BY t.date")
    List<Transaction> findAfterTransactions(@Param("customerId") long customerId, @Param("date") String date);

    @Query(value = "SELECT * FROM transaction WHERE customer_id = :customerId AND date IS NOT NULL AND date < :date ORDER BY date DESC LIMIT 1", nativeQuery = true)
    Transaction findPreviousTransaction(@Param("customerId") long customerId, @Param("date") String date);

    @Query("""
            SELECT new com.transaction.book.dto.responseDTO.TransactionResponse(
                t.id, t.amount, t.balanceAmount, t.date, t.detail, t.customer.name)
            FROM Transaction t
            WHERE (:query IS NULL OR (t.customer.name LIKE %:query% OR t.customer.mobileNo LIKE %:query%))
            AND (:startDate IS NULL OR t.date >= CONCAT(:startDate, ' 00:00:00'))
            AND (:endDate IS NULL OR t.date <= CONCAT(:endDate, ' 23:59:59'))
            ORDER BY t.date DESC
            """)
List<TransactionResponse> findAllTransactions(@Param("query") String query,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);



    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.amount>0")
    Double totalYouGot();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.amount<0")
    Double totalYouGave();

}
