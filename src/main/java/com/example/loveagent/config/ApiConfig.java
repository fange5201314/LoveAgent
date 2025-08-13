package com.example.loveagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * API配置类，用于管理API密钥和其他配置信息
 */
@Configuration
public class ApiConfig {
    
    @Value("${dashscope.api.key:}")
    private String apiKey;
    
    @Value("${dashscope.api.url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${dashscope.api.model:qwen-plus}")
    private String defaultModel;

    /**
     * 获取API密钥
     * @return API密钥
     */
    public String getApiKey() {
        // 优先使用环境变量，其次使用配置文件
        String envKey = System.getenv("DASHSCOPE_API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return apiKey;
    }
    
    /**
     * 获取API URL
     * @return API URL
     */
    public String getApiUrl() {
        return apiUrl;
    }
    
    /**
     * 获取默认模型名称
     * @return 默认模型名称
     */
    public String getDefaultModel() {
        return defaultModel;
    }
    
    /**
     * 验证API密钥是否已配置
     * @return 是否已配置
     */
    public boolean isApiKeyConfigured() {
        String key = getApiKey();
        return key != null && !key.isEmpty() && !key.equals("your-default-api-key");
    }
}