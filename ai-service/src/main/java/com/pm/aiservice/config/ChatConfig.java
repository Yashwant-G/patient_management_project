package com.pm.aiservice.config;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatConfig {

    private final ChatClient chatClient;

    public ChatConfig(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String call(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}