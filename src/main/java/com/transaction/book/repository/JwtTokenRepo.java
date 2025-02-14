package com.transaction.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.transaction.book.entities.JwtToken;

public interface JwtTokenRepo extends JpaRepository<JwtToken,Long>{
    
    @Query("SELECT j FROM JwtToken j where j.token=:token")
     JwtToken findbyToken(@Param("token") String token);
}
