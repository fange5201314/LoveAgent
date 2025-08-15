package com.example.loveagent.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpResponse {
    private String jsonrpc = "2.0";
    private String id;
    private Object result;
    private McpError error;

    @JsonProperty("meta")
    private Object meta;

    @Data
    public static class McpError {
        private int code;
        private String message;
        private Object data;
    }
}