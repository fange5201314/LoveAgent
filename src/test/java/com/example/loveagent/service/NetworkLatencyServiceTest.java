package com.example.loveagent.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "network.monitor.timeout=3000",
    "network.monitor.retries=2"
})
class NetworkLatencyServiceTest {

    @Autowired
    private NetworkLatencyService networkLatencyService;

    @Test
    void testCheckAllEndpoints() {
        Map<String, Long> latencies = networkLatencyService.checkAllEndpoints();
        
        assertNotNull(latencies);
        assertFalse(latencies.isEmpty());
        
        // 验证所有预期的端点都被检测了
        assertTrue(latencies.containsKey("香港"));
        assertTrue(latencies.containsKey("日本"));
        assertTrue(latencies.containsKey("新加坡"));
        assertTrue(latencies.containsKey("大陆"));
        
        System.out.println("延迟检测结果:");
        latencies.forEach((name, latency) -> 
            System.out.println(name + ": " + (latency > 0 ? latency + "ms" : "失败")));
    }

    @Test
    void testFindFastestEndpoint() {
        String fastestEndpoint = networkLatencyService.findFastestEndpoint();
        
        if (fastestEndpoint != null) {
            System.out.println("最快的端点: " + fastestEndpoint);
            assertTrue(fastestEndpoint.startsWith("https://"));
        } else {
            System.out.println("没有找到可用的端点");
        }
    }

    @Test
    void testGetCurrentFastestEndpoint() {
        String fastestEndpoint = networkLatencyService.getCurrentFastestEndpoint();
        
        if (fastestEndpoint != null) {
            System.out.println("当前最快的端点: " + fastestEndpoint);
            assertTrue(fastestEndpoint.startsWith("https://"));
        }
    }
}