package com.foodcrunch.foodster.recipemanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@SpringBootApplication
public class RecipeManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeManagerApplication.class, args);
    }

    private static final void firebaseInit() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/slowcooker-a725e-firebase-adminsdk-ofrx3-79cff3cd02.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://slowcooker-a725e.firebaseio.com")
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
