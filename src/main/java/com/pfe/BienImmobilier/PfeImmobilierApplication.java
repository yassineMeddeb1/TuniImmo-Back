package com.pfe.BienImmobilier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PfeImmobilierApplication {

    public static void main(String[] args) {
        SpringApplication.run(PfeImmobilierApplication.class, args);
    }

}
