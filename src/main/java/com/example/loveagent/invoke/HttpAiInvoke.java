package com.example.loveagent.invoke;

import com.example.loveagent.config.ApiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP AI调用类，用于与AI服务进行交互
 */
public class HttpAiInvoke {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(HttpAiInvoke.class.getName());

    /**
     * 发送聊天请求到AI服务
     * @param systemPrompt 系统提示信息
     * @param userPrompt 用户输入信息
     * @return AI服务的响应结果
     * @throws IOException 如果请求过程中发生异常
     */
    /**
     * 发送聊天请求到AI服务
     * @param systemPrompt 系统提示信息
     * @param userPrompt 用户输入信息
     * @return AI服务的响应结果
     * @throws IOException 如果请求过程中发生异常
     */
    public static String sendChatRequest(String systemPrompt, String userPrompt) throws IOException {
        // 验证API密钥
        String apiKey = ApiConfig.getApiKey();
        if (apiKey.equals("your-default-api-key")) {
            throw new IOException("API密钥未配置，请设置环境变量 DASHSCOPE_API_KEY");
        }
        if (apiKey.isEmpty()) {
            throw new IOException("API密钥为空，请检查环境变量 DASHSCOPE_API_KEY");
        }
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", ApiConfig.DEFAULT_MODEL);
            requestBody.set("messages", buildMessages(systemPrompt, userPrompt));

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + TestApiKey.API_KEY);

            // 记录请求信息
            logger.info("发送请求到AI服务: " + ApiConfig.API_URL);
            logger.info("请求头: " + headers.toString());
            logger.info("请求体: " + requestBody.toString());

            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    ApiConfig.API_URL,
                    requestEntity,
                    String.class
            );

            // 记录响应状态
            logger.info("响应状态码: " + response.getStatusCode());
            logger.info("响应体: " + response.getBody());

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
    private static ArrayNode buildMessages(String systemPrompt, String userPrompt) {
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

    /**
     * 主方法，用于测试AI调用
     * @param args 命令行参数
     */
    /**
     * 主方法，用于测试AI调用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            String systemPrompt = "You are a helpful assistant.";
            String userPrompt = "你是谁？";
            System.out.println("发送请求到AI服务...");
            String response = sendChatRequest(systemPrompt, userPrompt);
            System.out.println("AI响应结果：" + response);
        } catch (IOException e) {
            if (e.getMessage().contains("API密钥")) {
                System.err.println("错误: " + e.getMessage());
                System.err.println("解决方案:");
                System.err.println("1. 在Windows系统中，打开命令提示符并执行:");
                System.err.println("   setx DASHSCOPE_API_KEY \"你的API密钥\"\n");
                System.err.println("2. 重启命令提示符或IDE使环境变量生效");
            } else {
                System.err.println("HTTP调用失败: " + e.getMessage());
            }
            logger.log(Level.SEVERE, "HTTP调用失败", e);
        } catch (Exception e) {
            System.err.println("程序执行出错: " + e.getMessage());
            logger.log(Level.SEVERE, "程序执行出错", e);
        }
    }
}