package com.oneisnstep.mcpserver.config;

import com.oneisnstep.mcpserver.annotation.ToolBean;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ToolConfig {

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public ToolCallbackProvider toolCallbackProvider() {
        // 获取所有工具Bean（假设都在tools包下且有@Service注解）
        Map<String, Object> toolBeans = applicationContext.getBeansWithAnnotation(ToolBean.class);
        return MethodToolCallbackProvider.builder()
                .toolObjects(toolBeans.values().toArray())
                .build();
    }

}