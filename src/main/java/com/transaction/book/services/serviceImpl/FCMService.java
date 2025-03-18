package com.transaction.book.services.serviceImpl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.stereotype.Service;

@Service
public class FCMService {
    
    public void sendNotification(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()  // Send notification payload
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)  // Ensure high priority
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")  // Add sound
                                .setClickAction("OPEN_ACTIVITY")  // Handle click action in Android
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setAlert(ApsAlert.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()).build())
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification Sent: " + response);
        } catch (Exception e) {    
            e.printStackTrace();
        }
    }
}
