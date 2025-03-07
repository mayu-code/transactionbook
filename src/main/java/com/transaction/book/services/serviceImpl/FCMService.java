package com.transaction.book.services.serviceImpl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FCMService {
    
    public void sendNotification(String token, String title, String body) {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(token)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification Sent: " + response);
        } catch (Exception e) {    
            e.printStackTrace();
        }
    }
}
