package com.transaction.book.services.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.Remainder;
import com.transaction.book.repository.RemainderRepo;
import com.transaction.book.services.serviceInterface.RemainderService;


@Service
public class RemainderServiceImpl implements RemainderService{

    @Autowired
    private RemainderRepo remainderRepo;

    @Override
    public Remainder addRemainder(Remainder remainder) {
        return this.remainderRepo.save(remainder);
    }

    @Override
    public List<Remainder> getAllRemindersByCustomerId(long id) {
        return this.remainderRepo.findRemaindersByCustomerId(id);
    }

    @Override
    public void deleteRemainder(long id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteRemainder'");
    }

    @Override
    public Remainder getExactLastRemainder(long id) {
        String date = String.valueOf(LocalDateTime.now());
        return this.remainderRepo.findLastRemainder(id,date);
    }

    @Override
    public Remainder getRemainderByDate(String date, long id) {
        return this.remainderRepo.getRemainderByDate(date, id);
    }
    
}
