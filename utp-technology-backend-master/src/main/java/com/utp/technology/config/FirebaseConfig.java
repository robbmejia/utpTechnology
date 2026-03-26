package com.utp.technology.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            // Try to load from Render Secret File path first
            File secretFileRender = new File("/etc/secrets/service-account.json");
            File secretFileRoot = new File("service-account.json");
            
            if (secretFileRender.exists()) {
                System.out.println("LOG: Firebase Secret found at " + secretFileRender.getAbsolutePath());
                serviceAccount = new FileInputStream(secretFileRender);
            } else if (secretFileRoot.exists()) {
                System.out.println("LOG: Firebase Secret found at " + secretFileRoot.getAbsolutePath());
                serviceAccount = new FileInputStream(secretFileRoot);
            } else {
                System.out.println("LOG: Secret file not found in /etc/secrets/ or root. Falling back to classpath...");
                // Fallback to classpath for local development
                ClassPathResource resource = new ClassPathResource("service-account.json");
                serviceAccount = resource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore() throws IOException {
        return FirestoreClient.getFirestore(firebaseApp());
    }
}
