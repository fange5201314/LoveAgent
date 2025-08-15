package com.example.loveagent;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EndpointConnectivityTest {

    private static final List<String> TEST_ENDPOINTS = List.of(
            "https://hk.instcopilot-api.com",
            "https://jp.instcopilot-api.com", 
            "https://sg.instcopilot-api.com",
            "https://instcopilot-api.yinban.online"
    );

    @Test
    public void testAllEndpoints() {
        System.out.println("\n=== API端点连通性测试 ===\n");
        
        for (String endpoint : TEST_ENDPOINTS) {
            testEndpoint(endpoint);
            System.out.println();
        }
        
        System.out.println("=== 测试完成 ===\n");
    }

    private void testEndpoint(String baseUrl) {
        String name = getEndpointName(baseUrl);
        System.out.println("🌐 测试端点: " + name);
        System.out.println("   URL: " + baseUrl);
        
        try {
            // 测试根路径
            long startTime = System.currentTimeMillis();
            HttpResponse response = HttpRequest.get(baseUrl)
                    .timeout(10000)
                    .execute();
            long responseTime = System.currentTimeMillis() - startTime;
            
            System.out.println("   状态码: " + response.getStatus());
            System.out.println("   响应时间: " + responseTime + "ms");
            System.out.println("   连通性: " + (response.isOk() ? "✅ 成功" : "⚠️ 异常"));
            
            if (response.body() != null && !response.body().trim().isEmpty()) {
                String body = response.body().trim();
                if (body.length() > 200) {
                    body = body.substring(0, 200) + "...";
                }
                System.out.println("   响应内容: " + body);
            }
            
            // 测试API路径
            testApiPath(baseUrl);
            
        } catch (Exception e) {
            System.out.println("   连通性: ❌ 失败");
            System.out.println("   错误信息: " + e.getMessage());
        }
    }
    
    private void testApiPath(String baseUrl) {
        try {
            String apiUrl = baseUrl + "/v1/chat/completions";
            
            // 创建测试请求体
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", "gpt-3.5-turbo");
            requestBody.set("messages", List.of(
                new JSONObject().set("role", "user").set("content", "Hello")
            ));
            requestBody.set("max_tokens", 5);
            
            HttpResponse response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer test-key")
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(10000)
                    .execute();
            
            System.out.println("   API路径状态: " + response.getStatus());
            if (response.getStatus() == 401) {
                System.out.println("   API状态: 🔐 需要认证（端点正常）");
            } else if (response.getStatus() == 404) {
                System.out.println("   API状态: 📍 路径不存在");
            } else if (response.isOk()) {
                System.out.println("   API状态: ✅ API可用");
            }
            
        } catch (Exception e) {
            System.out.println("   API路径: ❌ 无法访问");
        }
    }
    
    private String getEndpointName(String url) {
        if (url.contains("hk.")) return "香港节点";
        if (url.contains("jp.")) return "日本节点";
        if (url.contains("sg.")) return "新加坡节点";
        if (url.contains("yinban")) return "大陆节点";
        return "未知节点";
    }
}