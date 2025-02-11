package com.transaction.book.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

// @Configuration
public class FirebaseConfig {

    // @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Load the Firebase JSON file from classpath
        InputStream serviceAccount = new ClassPathResource("transactionbook-4bb66-firebase-adminsdk-fbsvc-9066517923.json").getInputStream();   
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}
