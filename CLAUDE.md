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

## 项目问题与改进建议

### 当前存在的问题

1. **安全隐患**：
   - API密钥直接硬编码在`TestApiKey.java`中
   - `HttpAiInvoke`类中使用硬编码的API密钥而非从`ApiConfig`获取
   - 缺少请求参数验证和安全过滤

2. **代码结构问题**：
   - HTTP调用和SDK调用方式没有统一的接口抽象
   - 缺少服务层（Service Layer）的抽象
   - 错误处理机制不完善

3. **功能局限性**：
   - 仅支持简单的单轮对话
   - 缺少对话历史管理
   - 没有实现异步调用
   - 缺少结果缓存机制

### 改进建议

1. **安全性改进**：
   - 移除所有硬编码的API密钥，统一使用环境变量或配置文件
   - 实现API密钥的加密存储
   - 添加请求参数验证和安全过滤

2. **架构优化**：
   - 创建统一的AI服务接口，实现策略模式
   - 添加服务层抽象，分离业务逻辑和API调用
   - 实现更完善的错误处理和重试机制

3. **功能扩展**：
   - 支持多轮对话和对话历史管理
   - 实现异步调用API
   - 添加结果缓存机制
   - 支持更多的AI模型和参数配置
   - 开发简单的Web界面用于交互测试

4. **开发体验**：
   - 增加单元测试和集成测试
   - 完善API文档
   - 添加性能监控
   - 实现CI/CD流程

## 使用示例

### HTTP方式调用

```java
// 导入必要的类
import com.example.loveagent.invoke.HttpAiInvoke;
import java.io.IOException;

public class Example {
    public static void main(String[] args) {
        try {
            // 系统提示和用户输入
            String systemPrompt = "You are a helpful assistant.";
            String userPrompt = "你是谁？";

            // 调用AI服务
            String response = HttpAiInvoke.sendChatRequest(systemPrompt, userPrompt);

            // 处理响应
            System.out.println("AI响应结果：" + response);
        } catch (IOException e) {
            System.err.println("调用AI服务时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### SDK方式调用

```java
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.example.loveagent.invoke.SdkAiInvoke;

public class Example {
    public static void main(String[] args) {
        try {
            GenerationResult result = SdkAiInvoke.callWithMessage();
            System.out.println(JsonUtils.toJson(result));
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.err.println("调用AI服务时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
```