package com.transaction.book.services.serviceImpl;

import org.springframework.scheduling.annotation.Scheduled;

import com.transaction.book.services.serviceInterface.ScheduledMethodService;

public class ScheduledMethodServiceImpl implements ScheduledMethodService{

    private final FCMService fcmService;
    private final UserServiceImpl userServiceImpl;

    public ScheduledMethodServiceImpl(FCMService fcmService, UserServiceImpl userServiceImpl) {
        this.fcmService = fcmService;
        this.userServiceImpl = userServiceImpl;
    }

    @Scheduled(cron = "0 30 20 * * ?")
    @Override
    public void sendMorningNotification() {
        for(String userToken:this.userServiceImpl.getAllFcmTokens()){
            fcmService.sendNotification(userToken, "Good Morning!", "Start your day with a new update.");
        }
    }
    
}
