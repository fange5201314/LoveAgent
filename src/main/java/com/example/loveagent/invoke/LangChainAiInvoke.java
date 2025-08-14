package com.example.loveagent.invoke;

import com.example.loveagent.config.ApiConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;


public class LangChainAiInvoke {
    public static void main(String[] args) {
        ChatLanguageModel chatLanguageModel = QwenChatModel.builder()
                .apiKey(new ApiConfig().getApiKey())
                .modelName("qwen-plus")
                .build();
        String chat = chatLanguageModel.chat("你是谁？");
        System.out.println("chat = " + chat);
    }
}
