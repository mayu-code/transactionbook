package com.transaction.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transaction.book.entities.OtpEntry;

import jakarta.transaction.Transactional;


public interface OtpRepo extends JpaRepository<OtpEntry,Long>{
    Optional<OtpEntry> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}
