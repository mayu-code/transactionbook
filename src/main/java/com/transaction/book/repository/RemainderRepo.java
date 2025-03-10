package com.transaction.book.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.transaction.book.entities.Remainder;

public interface RemainderRepo extends JpaRepository<Remainder,Long>{
    
    @Query("SELECT r FROM Remainder r WHERE r.customer.id=:id AND r.isDelete=false ORDER BY r.addedDate DESC")
    List<Remainder> findRemaindersByCustomerId(@Param("id")long id);

    @Query(value = "SELECT * FROM remainder r WHERE r.customer_id = :id AND r.is_delete = false AND r.added_date<:now ORDER BY r.added_date DESC LIMIT 1", 
       nativeQuery = true)
    Remainder findLastRemainder(@Param("id") long id, @Param("now")String now);

    @Query("SELECT r FROM Remainder r WHERE r.customer.id=:id AND r.isDelete=false AND r.dueDate=:date")
    Remainder getRemainderByDate(@Param("date")String date,@Param("id")long id);




}
