package com.transaction.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.transaction.book.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User,Long>{


    @Query(value = "SELECT * From User WHERE id=:id AND delete_flag=false", nativeQuery=true)
    User findById(long id);

    @Query("SELECT u FROM User u Where u.mobileNo=:mobileNo and u.deleteFlag=false")
    User findByMobileNo(String mobileNo);

    @Query("SELECT u FROM User u Where u.email=:email and u.deleteFlag=false")
    User findByEmail(String email);
    boolean existsByIdNotNull();
    
    @Query("SELECT u FROM User u Where u.approved=false and u.deleteFlag=false")
    List<User> findApprovalRequest();

    @Query("SELECT u.fcmToken FROM User u WHERE u.deleteFlag=false AND u.approved=true")
    List<String> getAllFcmToken();
}
