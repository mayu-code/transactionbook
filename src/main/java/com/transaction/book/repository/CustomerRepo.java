package com.transaction.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.transaction.book.dto.responseDTO.CusotomerFullResponse;
import com.transaction.book.dto.responseDTO.CustomerResponse;
import com.transaction.book.entities.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT * FROM Customer WHERE mobileNo=:mobileNo AND delete_flag=false",nativeQuery = true)
    Customer findByMobileNo(String mobileNo);

    @Query(value = "SELECT * FROM Customer WHERE id=:id AND delete_flag=false",nativeQuery = true)
    Customer findById(long id);

    @Query("SELECT c FROM Customer c WHERE c.deleteFlag=false ORDER BY c.updateDate DESC")
    List<Customer> findAllCustomers();

    @Query("SELECT SUM(c.amount) FROM Customer c WHERE c.amount<0 and c.deleteFlag=false")
    Double getTotalGetAmount();

    @Query("SELECT SUM(c.amount) FROM Customer c WHERE c.amount>0 AND c.deleteFlag=false")
    Double getTotalGaveAmount();

    @Query("""
                SELECT new com.transaction.book.dto.responseDTO.CustomerResponse(
                    c.id, c.name, c.mobileNo, c.gstinNo, c.amount, c.dueDate, c.updateDate)
                FROM Customer c
                WHERE (:query IS NULL OR (c.name LIKE %:query% OR c.mobileNo LIKE %:query%))
                AND (
                    (:gave = true AND c.amount > 0) OR
                    (:get = true AND c.amount < 0) OR
                    (:settel = true AND c.amount = 0) OR
                    (:gave = false AND :get = false AND :settel = false)
                )
                AND c.deleteFlag=false
                ORDER BY c.updateDate DESC
            """)
    List<CustomerResponse> findAllCustomerResponse(@Param("query") String query,
            @Param("gave") boolean gave,
            @Param("get") boolean get,
            @Param("settel") boolean settel);

    @Query("SELECT new com.transaction.book.dto.responseDTO.CusotomerFullResponse(c.id, c.name, c.mobileNo, c.gstinNo, c.amount, c.dueDate, c.updateDate, c.address,c.reference) FROM Customer c WHERE c.id = :id AND c.deleteFlag=false")
    CusotomerFullResponse findCustomerResponseById(@Param("id") long id);

    @Query("""
                SELECT new com.transaction.book.dto.responseDTO.CustomerResponse(
                    c.id, c.name, c.mobileNo, c.gstinNo, c.amount, c.dueDate, c.updateDate)
                FROM Customer c
                WHERE (c.dueDate=:today)
                AND c.deleteFlag=false
            """)
    List<CustomerResponse> findTodaysDueDateCusotmers(@Param("today") String today);

    @Query("SELECT new com.transaction.book.dto.responseDTO.CusotomerFullResponse(c.id, c.name, c.mobileNo, c.gstinNo, c.amount, c.dueDate, c.updateDate, c.address,c.reference) FROM Customer c WHERE c.name = :name AND c.deleteFlag=false")
    CusotomerFullResponse findCustomerResponseByName(@Param("name") String  name);
}
