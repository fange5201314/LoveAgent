package com.example.loveagent.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class McpRequest {
    private String jsonrpc = "2.0";
    private String id;
    private String method;
    private Map<String, Object> params;

    @JsonProperty("meta")
    private Map<String, Object> meta;
}