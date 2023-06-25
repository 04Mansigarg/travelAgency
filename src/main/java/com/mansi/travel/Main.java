package com.mansi.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mansi"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}