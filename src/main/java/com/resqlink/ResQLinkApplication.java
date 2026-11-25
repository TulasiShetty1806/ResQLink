package com.resqlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResQLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResQLinkApplication.class, args);
    }
}
