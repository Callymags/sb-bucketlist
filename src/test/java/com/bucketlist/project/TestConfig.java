package com.bucketlist.project;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import io.github.cdimascio.dotenv.Dotenv;

@TestConfiguration
public class TestConfig {

    @Bean
    public void setupEnvVariables() {
        Dotenv dotenv = Dotenv.configure().directory("../").load();
        System.setProperty("EXPERIENCE_POSTGRES_USER", dotenv.get("EXPERIENCE_POSTGRES_USER"));
        System.setProperty("EXPERIENCE_POSTGRES_PASSWORD", dotenv.get("EXPERIENCE_POSTGRES_PASSWORD"));
        System.setProperty("EXPERIENCE_POSTGRES_DB", dotenv.get("EXPERIENCE_POSTGRES_DB"));

    }
}

