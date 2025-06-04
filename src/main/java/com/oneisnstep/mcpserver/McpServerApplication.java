package com.example.mcpserver;

import com.example.mcpserver.tools.SimpleTool;
import com.example.mcpserver.tools.WeatherTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider toolCallbackProvider(WeatherTool weatherTool, SimpleTool simpleTool) {
        // 创建工具回调提供者，确保工具可见
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherTool, simpleTool)
                .build();
    }

}
