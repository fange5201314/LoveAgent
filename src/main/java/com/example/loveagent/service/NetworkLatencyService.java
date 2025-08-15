package com.example.loveagent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NetworkLatencyService {

    @Value("${network.monitor.endpoints.hongkong:https://hk.instcopilot-api.com}")
    private String hongkongEndpoint;

    @Value("${network.monitor.endpoints.japan:https://jp.instcopilot-api.com}")
    private String japanEndpoint;

    @Value("${network.monitor.endpoints.singapore:https://sg.instcopilot-api.com}")
    private String singaporeEndpoint;

    @Value("${network.monitor.endpoints.mainland:https://instcopilot-api.yinban.online}")
    private String mainlandEndpoint;

    @Value("${network.monitor.timeout:5000}")
    private int timeoutMs;

    @Value("${network.monitor.retries:3}")
    private int retries;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final String ENV_VAR_NAME = "ANTHROPIC_BASE_URL";

    @Scheduled(cron = "${network.monitor.schedule:0 0 8 * * ?}")
    public void scheduledLatencyCheck() {
        log.info("开始执行定时延迟检测任务");
        String fastestEndpoint = findFastestEndpoint();
        if (fastestEndpoint != null) {
            setEnvironmentVariable(fastestEndpoint);
        }
    }

    public Map<String, Long> checkAllEndpoints() {
        Map<String, String> endpoints = getEndpoints();
        Map<String, Long> latencies = new HashMap<>();

        List<CompletableFuture<Void>> futures = endpoints.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    String name = entry.getKey();
                    String url = entry.getValue();
                    long latency = measureLatency(url);
                    synchronized (latencies) {
                        latencies.put(name, latency);
                    }
                }, executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return latencies;
    }

    public String findFastestEndpoint() {
        Map<String, Long> latencies = checkAllEndpoints();
        
        log.info("延迟检测结果: {}", latencies);

        return latencies.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min(Map.Entry.comparingByValue())
                .map(entry -> getEndpoints().get(entry.getKey()))
                .orElse(null);
    }

    private long measureLatency(String urlString) {
        long totalLatency = 0;
        int successfulAttempts = 0;

        for (int attempt = 0; attempt < retries; attempt++) {
            try {
                long startTime = System.currentTimeMillis();
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(timeoutMs);
                connection.setReadTimeout(timeoutMs);
                connection.setInstanceFollowRedirects(false);
                
                connection.getResponseCode();
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                
                totalLatency += latency;
                successfulAttempts++;
                
                connection.disconnect();
                
            } catch (IOException e) {
                log.warn("测试 {} 延迟失败 (尝试 {}/{}): {}", urlString, attempt + 1, retries, e.getMessage());
            }
        }

        if (successfulAttempts == 0) {
            log.error("所有尝试都失败了: {}", urlString);
            return -1;
        }

        long averageLatency = totalLatency / successfulAttempts;
        log.info("URL: {} 平均延迟: {}ms (成功尝试: {}/{})", urlString, averageLatency, successfulAttempts, retries);
        
        return averageLatency;
    }

    private void setEnvironmentVariable(String baseUrl) {
        try {
            String command = String.format("setx %s \"%s\"", ENV_VAR_NAME, baseUrl);
            log.info("设置环境变量: {}", command);
            
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("成功设置环境变量 {} = {}", ENV_VAR_NAME, baseUrl);
            } else {
                log.error("设置环境变量失败，退出码: {}", exitCode);
            }
            
        } catch (Exception e) {
            log.error("执行setx命令时发生错误", e);
        }
    }

    private Map<String, String> getEndpoints() {
        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("香港", hongkongEndpoint);
        endpoints.put("日本", japanEndpoint);
        endpoints.put("新加坡", singaporeEndpoint);
        endpoints.put("大陆", mainlandEndpoint);
        return endpoints;
    }

    public String getCurrentFastestEndpoint() {
        return findFastestEndpoint();
    }
}