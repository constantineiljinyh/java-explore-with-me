package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${STAT_SERVER_URL:http://localhost:9090}")
    private String statClientUrl;

    @Bean
    public String statClientUrl() {
        return statClientUrl;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}