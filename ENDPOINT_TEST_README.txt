# 端点测试说明

我已经为你创建了测试这些API端点的脚本，有多种运行方式：

## 方式1：运行批处理文件（推荐）
双击运行项目根目录下的 `run_endpoint_test.bat` 文件

## 方式2：使用Maven运行JUnit测试
在项目根目录下运行：
```cmd
mvn test -Dtest=EndpointConnectivityTest
```

## 方式3：直接运行Java类
```cmd
mvn compile exec:java -Dexec.mainClass="com.example.loveagent.EndpointTestScript"
```

## 测试的端点
- 香港：https://hk.instcopilot-api.com
- 日本：https://jp.instcopilot-api.com  
- 新加坡：https://sg.instcopilot-api.com
- 大陆：https://instcopilot-api.yinban.online

## 测试内容
- 基本连通性测试
- 响应时间测量
- API路径 `/v1/chat/completions` 可用性检查
- 错误信息记录

测试结果会显示每个端点的状态、响应时间和可用性。