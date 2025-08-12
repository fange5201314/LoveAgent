package com.example.loveagent.config;

/**
 * API配置类，用于管理API密钥和其他配置信息
 */
public class ApiConfig {
    // API密钥，可以从环境变量或配置文件中读取
    private static final String API_KEY = System.getenv("DASHSCOPE_API_KEY") != null ?
            System.getenv("DASHSCOPE_API_KEY") : "sk-25c24c7ecacf41dd9da7471d89d32c15";

    // API端点URL
    public static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    // 默认模型名称
    public static final String DEFAULT_MODEL = "qwen-plus";

    /**
     * 获取API密钥
     * @return API密钥
     */
    public static String getApiKey() {
        return API_KEY;
    }
}