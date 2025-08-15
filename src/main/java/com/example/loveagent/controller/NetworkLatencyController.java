package com.example.loveagent.controller;

import com.example.loveagent.service.NetworkLatencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/network")
@RequiredArgsConstructor
@Tag(name = "网络延迟监控", description = "网络延迟检测和环境变量设置相关接口")
public class NetworkLatencyController {

    private final NetworkLatencyService networkLatencyService;

    @GetMapping("/latency")
    @Operation(summary = "检查所有端点延迟", description = "检测所有配置的端点的网络延迟")
    @ApiResponse(responseCode = "200", description = "延迟检测成功")
    public ResponseEntity<Map<String, Object>> checkLatency() {
        try {
            Map<String, Long> latencies = networkLatencyService.checkAllEndpoints();
            String fastestEndpoint = networkLatencyService.findFastestEndpoint();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("latencies", latencies);
            response.put("fastestEndpoint", fastestEndpoint);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查延迟时发生错误", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/set-fastest")
    @Operation(summary = "设置最快端点", description = "检测并自动设置ANTHROPIC_BASE_URL环境变量为最快的端点")
    @ApiResponse(responseCode = "200", description = "环境变量设置成功")
    public ResponseEntity<Map<String, Object>> setFastestEndpoint() {
        try {
            String fastestEndpoint = networkLatencyService.findFastestEndpoint();
            
            Map<String, Object> response = new HashMap<>();
            if (fastestEndpoint != null) {
                // 触发设置环境变量的逻辑
                networkLatencyService.scheduledLatencyCheck();
                response.put("success", true);
                response.put("fastestEndpoint", fastestEndpoint);
                response.put("message", "已设置ANTHROPIC_BASE_URL环境变量");
            } else {
                response.put("success", false);
                response.put("message", "无法找到可用的端点");
            }
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("设置最快端点时发生错误", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/fastest")
    @Operation(summary = "获取最快端点", description = "获取当前检测到的最快端点，不设置环境变量")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public ResponseEntity<Map<String, Object>> getFastestEndpoint() {
        try {
            String fastestEndpoint = networkLatencyService.getCurrentFastestEndpoint();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fastestEndpoint", fastestEndpoint);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取最快端点时发生错误", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}