package com.example.loveagent.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "mcp")
public class McpProperties {
    
    private Server server = new Server();
    private List<ExternalServer> externalServers;

    @Data
    public static class Server {
        private boolean enabled = true;
        private String name = "love-agent-mcp";
        private String version = "1.0.0";
        private String description = "LoveAgent MCP Server - AI capabilities exposure";
        private int port = 8124;
        private String path = "/mcp";
    }

    @Data
    public static class ExternalServer {
        private String name;
        private String command;
        private List<String> args;
        private Map<String, String> env;
        private boolean enabled = true;
    }
}