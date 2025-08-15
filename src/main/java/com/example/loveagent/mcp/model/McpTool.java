package com.example.loveagent.mcp.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class McpTool {
    private String name;
    private String description;
    private ToolSchema inputSchema;

    @Data
    public static class ToolSchema {
        private String type = "object";
        private Map<String, Object> properties;
        private List<String> required;
    }
}