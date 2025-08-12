# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LoveAgent is a Spring Boot application that provides AI integration capabilities using Alibaba's DashScope (通义千问) AI service. The application offers both HTTP-based and SDK-based approaches for interacting with AI models.

## Build and Development Commands

### Building and Running
```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

### Running Single Tests
```bash
# Run specific test class
./mvnw test -Dtest=LoveAgentApplicationTests

# Run specific test method
./mvnw test -Dtest=LoveAgentApplicationTests#contextLoads
```

## Architecture and Code Structure

### Core Components

**AI Invocation Layer** (`com.example.loveagent.invoke`):
- `HttpAiInvoke`: Direct HTTP client implementation using Hutool for API calls to DashScope
- `SdkAiInvoke`: Official SDK-based implementation using alibaba/dashscope-sdk-java
- `TestApiKey`: Contains API key configuration (should be externalized for production)

**Web Layer** (`com.example.loveagent.controller`):
- `HealthController`: Basic health check endpoint at `/api/health`

### Key Dependencies
- **Spring Boot 3.5.4**: Core framework with Java 21
- **DashScope SDK 2.21.2**: Alibaba's official AI SDK
- **Hutool 5.8.37**: Utility library for HTTP operations and JSON handling
- **Knife4j 4.4.0**: API documentation (Swagger UI available at `/api/swagger-ui.html`)
- **Lombok 1.18.36**: Code generation for boilerplate reduction

### Application Configuration
- **Server Port**: 8123
- **Context Path**: `/api`
- **API Documentation**: Available at `http://localhost:8123/api/swagger-ui.html`
- **Health Check**: `GET /api/health`

### AI Integration Patterns
The application demonstrates two approaches for AI integration:
1. **HTTP Approach**: Raw HTTP calls using Hutool's HttpRequest for maximum control
2. **SDK Approach**: Using DashScope's official SDK for simplified integration

Both approaches target the `qwen-plus` model and follow the ChatGPT-compatible API format.

### Security Considerations
- API keys are currently hardcoded in `TestApiKey.java` - externalize to environment variables for production
- The application uses system environment variables in `HttpAiInvoke` but falls back to hardcoded values