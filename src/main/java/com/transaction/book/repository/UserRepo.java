package com.transaction.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.transaction.book.entities.User;

public interface UserRepo extends JpaRepository<User,Long>{

    User findByMobileNo(String mobileNo);
    User findByEmail(String email);
    boolean existsByIdNotNull();
    
    @Query("SELECT u FROM User u Where u.approved=false")
    List<User> findApprovalRequest();

    @Query("SELECT u.fcmToken FROM User u")
    List<String> getAllFcmToken();
}
