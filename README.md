# LoveAgent 项目说明

## 项目概述
LoveAgent是一个简单的Java客户端，用于与AI服务进行交互。它提供了一个封装好的HTTP客户端，可以方便地调用AI聊天服务。

## 项目结构
```
loveAgent/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── loveAgent/
│   │   │               ├── config/
│   │   │               │   └── ApiConfig.java
│   │   │               └── invoke/
│   │   │                   └── HttpAiInvoke.java
│   │   └── resources/
│   └── test/
├── pom.xml
└── README.md
```

## 核心功能

### 1. API配置管理
`ApiConfig`类用于管理API相关的配置信息：
- API密钥（从环境变量`DASHSCOPE_API_KEY`读取）
- API端点URL
- 默认模型名称

### 2. AI服务调用
`HttpAiInvoke`类提供了以下功能：
- `sendChatRequest`: 发送聊天请求到AI服务
- `buildMessages`: 构建请求消息数组
- `main`: 主方法，用于测试AI调用

## 使用方法

### 1. 配置API密钥
  程序需要使用有效的阿里云灵机API密钥才能正常工作。在运行程序前，必须设置环境变量`DASHSCOPE_API_KEY`，值为你的API密钥。

  - **Windows系统配置方法：**
    1. 打开命令提示符（以管理员身份运行）
    2. 执行以下命令：
       ```powershell
       setx DASHSCOPE_API_KEY "你的API密钥"
       ```
    3. 重启命令提示符或IDE使环境变量生效

  - **验证配置是否成功：**
    打开新的命令提示符，执行以下命令：
    ```powershell
    echo %DASHSCOPE_API_KEY%
    ```
    如果能看到你设置的API密钥，则配置成功。

### 2. 运行程序
直接运行 `HttpAiInvoke` 类的 `main` 方法，或通过以下方式调用：
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

### 3. 查看日志
程序会记录详细的请求和响应信息，有助于调试HTTP调用问题。

## 依赖
项目使用以下依赖：
- Hutool HTTP客户端库
- Hutool JSON处理库

## 注意事项
1. 确保在运行程序前设置了正确的API密钥
2. 项目使用的是阿里云DashScope API，需要确保你的API密钥有相应的权限
3. 如果需要修改API端点或默认模型，可以在`ApiConfig`类中进行修改

## 可能的改进
1. 添加更多的错误处理和重试机制
2. 支持更多的AI模型和API参数
3. 添加缓存机制以提高性能
4. 实现异步调用功能