package com.pfe.BienImmobilier;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PfeImmobilierApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
        System.setProperty("STRIPE_PUBLISHABLE_KEY", dotenv.get("STRIPE_PUBLISHABLE_KEY"));
        System.setProperty("STRIPE_WEBHOOK_SECRET", dotenv.get("STRIPE_WEBHOOK_SECRET"));
        SpringApplication.run(PfeImmobilierApplication.class, args);
    }

}
