package com.example.loveagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;
    @Test
    void testChat(){
        String chatID = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是孙艺凡";
        String answer = loveApp.doChat(message, chatID);
        //第二轮
        message = "我想让我另一半更加爱我";
        answer = loveApp.doChat(message, chatID);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我刚才跟你说过我叫什么，帮我回忆一下";
        answer = loveApp.doChat(message, chatID);
        Assertions.assertNotNull(answer);
    }

}