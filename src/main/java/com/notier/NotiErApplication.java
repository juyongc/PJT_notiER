package com.notier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NotiErApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotiErApplication.class, args);
    }

}
