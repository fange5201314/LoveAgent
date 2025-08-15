package com.example.loveagent.mcp.model;

import lombok.Data;

@Data
public class McpResource {
    private String uri;
    private String name;
    private String description;
    private String mimeType;
}