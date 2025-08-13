package com.example.loveagent.invoke;

import com.example.loveagent.config.ApiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP AI调用类，用于与AI服务进行交互
 */
@Component
public class HttpAiInvoke {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(HttpAiInvoke.class.getName());
    
    @Autowired
    private ApiConfig apiConfig;

    /**
     * 发送聊天请求到AI服务
     * @param systemPrompt 系统提示信息
     * @param userPrompt 用户输入信息
     * @return AI服务的响应结果
     * @throws IOException 如果请求过程中发生异常
     */
    public String sendChatRequest(String systemPrompt, String userPrompt) throws IOException {
        // 验证API密钥
        if (!apiConfig.isApiKeyConfigured()) {
            throw new IOException("API密钥未配置，请在application-local.yml中设置dashscope.api.key或设置环境变量DASHSCOPE_API_KEY");
        }
        
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", apiConfig.getDefaultModel());
            requestBody.set("messages", buildMessages(systemPrompt, userPrompt));

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiConfig.getApiKey());

            // 记录请求信息
            logger.info("发送请求到AI服务: " + apiConfig.getApiUrl());

            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiConfig.getApiUrl(),
                    requestEntity,
                    String.class
            );

            // 记录响应状态
            logger.info("响应状态码: " + response.getStatusCode());

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IOException("请求失败，状态码：" + response.getStatusCode() + "，响应体：" + response.getBody());
            }
        } catch (RestClientException e) {
            logger.log(Level.SEVERE, "REST客户端异常: " + e.getMessage(), e);
            throw new IOException("调用AI服务失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "未知异常: " + e.getMessage(), e);
            throw new IOException("调用AI服务发生未知错误: " + e.getMessage(), e);
        }
    }

    /**
     * 构建消息数组
     * @param systemPrompt 系统提示信息
     * @param userPrompt 用户输入信息
     * @return 消息数组JSON
     */
    private ArrayNode buildMessages(String systemPrompt, String userPrompt) {
        ArrayNode messages = objectMapper.createArrayNode();

        // 添加系统消息
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        // 添加用户消息
        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        return messages;
    }
}