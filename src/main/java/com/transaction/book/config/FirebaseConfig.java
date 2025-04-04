// package com.transaction.book.config;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;

// import java.io.IOException;
// import java.io.InputStream;

// @Configuration
// public class FirebaseConfig {

//     @Value("${firebase.config.file}")
//     private String firebasePath;

//     @Bean
//     public FirebaseApp initializeFirebase() throws IOException {

//         // Load the Firebase JSON file from classpath
//         InputStream serviceAccount = new ClassPathResource(firebasePath).getInputStream();   
//         FirebaseOptions options = new FirebaseOptions.Builder()
//                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                 .build();

//         if (FirebaseApp.getApps().isEmpty()) {
//             return FirebaseApp.initializeApp(options);
//         } else {
//             return FirebaseApp.getInstance();
//         }
//     }
// }
