package com.example.loveagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoveAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoveAgentApplication.class, args);
    }

}
