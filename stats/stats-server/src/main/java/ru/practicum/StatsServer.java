package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatsServer {
    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        SpringApplication.run(StatsServer.class, args);
    }
}
