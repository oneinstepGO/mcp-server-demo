# 简单 MCP Server 示例 (Java + Spring Boot + Spring AI)

## 项目目标

本项目旨在开发一个简单的模型上下文协议 (MCP) 服务器。此服务器将作为如何在 Java 和 Spring Boot 环境下构建 MCP 服务器的示例，使用
sse 进行通信。

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 3.3.5
- **Spring AI**: `spring-ai-starter-mcp-server-webmvc` 用于 MCP 服务器功能
- **构建工具**: Maven
- **通信协议**: sse

## 核心依赖

```xml

<dependencyManagement>
   <dependencies>
      <dependency>
         <groupId>io.modelcontextprotocol.sdk</groupId>
         <artifactId>mcp-bom</artifactId>
         <version>0.10.0</version>
         <type>pom</type>
         <scope>import</scope>
      </dependency>
   </dependencies>
</dependencyManagement>

<dependencies>
<!-- Spring Boot Starter -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter</artifactId>
</dependency>

<!-- Spring Boot Web Starter for SSE -->
<dependency>
   <groupId>org.springframework.ai</groupId>
   <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
   <version>1.0.0</version>
</dependency>

</dependencies>
```

## 关键组件

1. **MCP 服务器配置**:

   - 设置 `FastMCP` 或等效的 Spring AI 组件来初始化 MCP 服务器。
   - 服务器通过 sse 进行通信。

2. **示例工具 (Tools)**:

   - 实现了以下工具：
     - `WeatherTool`: 查询天气预报和天气警报
     - `SimpleTool`: 提供回声、简单数学和时间戳功能
   - 工具的定义遵循 MCP 规范，使用 Java 类型提示和注解来自动生成工具定义。

## 项目结构

```
cem_mcp_server
  ├── src/main/java/com/example/mcpserver
  │   ├── McpServerApplication.java        # 主应用程序类
  │   └── tools/                           # 工具实现
  │       ├── WeatherTool.java             # 天气工具
  │       └── SimpleTool.java              # 简单工具
  ├── src/main/resources
  │   └── application.yml                  # 应用配置
  └── pom.xml                              # Maven 配置
```

# 编译和运行

## 使用脚本运行

```bash
./run.sh
```

## 或者使用 Maven 命令

```bash
mvn clean package spring-boot:run
```

# 测试和调试

## 服务器端点

- **连接根端点**: `http://localhost:8080/sse` - 连接MCP服务器

## 测试工具

可以使用支持 MCP 协议的客户端（如 Cursor 编辑器）连接到 MCP 服务器。服务器提供以下工具：

1. **天气工具**:

   - `getWeatherForecastByLocation`: 获取指定经纬度的天气预报
   - `getAlerts`: 获取指定州的天气警报

2. **简单工具**:
   - `echoTool`: 回声工具，返回输入的消息
   - `simpleMathTool`: 计算两个数字的和
   - `timestampTool`: 返回当前时间

# 与 Cursor 集成

在 Cursor 编辑器中，可以通过以下步骤连接到此 MCP 服务器：

0. 项目下新增文件 `.cursor/mcp.json`。
      ```json
      {
        "mcpServers": {
          "simple-mcp-server": {
            "name": "本地 MCP 服务器",
            "url": "http://localhost:8080/sse",
            "enabled": true
          }
        }
      }
      ```
1. 打开 Cursor 设置
2. 找到 MCP 相关配置，刷新本MCP工具。
3. 现在可以在 Cursor 中使用本服务器提供的工具


