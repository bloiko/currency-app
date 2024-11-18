package com.currency.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyAppApplication.class, args);
    }

}
