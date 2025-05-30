package com.pfe.BienImmobilier.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Base de données
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

        // JWT
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Mail
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));

        // Stripe
        System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
        System.setProperty("STRIPE_PUBLISHABLE_KEY", dotenv.get("STRIPE_PUBLISHABLE_KEY"));
        System.setProperty("STRIPE_WEBHOOK_SECRET", dotenv.get("STRIPE_WEBHOOK_SECRET"));
    }
}
