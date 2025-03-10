package com.transaction.book.services.serviceInterface;

import java.util.List;

import com.transaction.book.entities.Remainder;

public interface RemainderService {
    Remainder addRemainder(Remainder remainder);
    List<Remainder> getAllRemindersByCustomerId(long id);
    void deleteRemainder(long id);
    Remainder getExactLastRemainder(long id);
    Remainder getRemainderByDate(String date,long id);
    
}
