package com.example.loveagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;
    public static final String SYSTEM_PROMPT = "你是一个专业的恋爱顾问助手。";

    /**
     * 初始化基于内存的AI客户端
     * @param chatModel 大模型
     */
    public LoveApp(ChatModel chatModel){
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = (ChatClient) ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))//对所有请求拦截有效
                .build();
//        chatClient.prompt()
//                .advisors();//对该请求单词拦截有效
    }

    /**
     * AI的基础对话功能（支持多轮对话）
     * @param message 消息
     * @param chatId 对话ID
     * @return AI返回内容
     */
    public String doChat(String message, String  chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info(text);
        return text;
    }
}
