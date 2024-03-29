package com.foodcrunch.foodster.recipemanager.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Primary
    @Bean
    public void firebaseInit() {
        try {
            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext();
            Resource file = appContext.getResource("classpath:slowcooker-a725e-firebase-adminsdk-ofrx3-79cff3cd02.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(file.getInputStream()))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException error) {
            error.printStackTrace();
            log.error("FIREBASE ERROR: " + error.getMessage());
        }
    }
}
