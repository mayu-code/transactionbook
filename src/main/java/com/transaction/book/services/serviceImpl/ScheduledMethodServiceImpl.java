package com.transaction.book.services.serviceImpl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.transaction.book.services.serviceInterface.ScheduledMethodService;

@Service
public class ScheduledMethodServiceImpl implements ScheduledMethodService{

    private final FCMService fcmService;
    private final UserServiceImpl userServiceImpl;

    public ScheduledMethodServiceImpl(FCMService fcmService, UserServiceImpl userServiceImpl) {
        this.fcmService = fcmService;
        this.userServiceImpl = userServiceImpl;
    }

    @Scheduled(cron = "0 0 10 * * ?")
    @Override
    public void sendMorningNotification() {
        for(String userToken:this.userServiceImpl.getAllFcmTokens()){
            fcmService.sendNotification(userToken, "Due Date Reminder", "Reminder regarding money !");
        }
    }
    
}
