package com.oneisnstep.mcpserver.tools;

import com.oneisnstep.mcpserver.annotation.ToolBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ToolBean
public class SimpleTool {

    /**
     * 回声工具，返回输入的消息
     */
    @Tool(name = "echoTool", description = "回声工具，返回输入的消息")
    public String echoTool(@ToolParam(description = "要回显的消息") String message) {
        log.info("Echo tool called with message: {}", message);
        return "回声: " + message;
    }

    /**
     * 简单数学工具，计算两个数字的和
     */
    @Tool(name = "simpleMathTool", description = "简单数学工具，计算两个数字的和")
    public double simpleMathTool(
            @ToolParam(description = "第一个数字") double a,
            @ToolParam(description = "第二个数字") double b) {
        log.info("Math tool called with a={}, b={}", a, b);
        return a + b;
    }

    /**
     * 时间戳工具，返回当前时间
     */
    @Tool(name = "timestampTool", description = "时间戳工具，返回当前时间")
    public String timestampTool() {
        String timestamp = java.time.LocalDateTime.now().toString();
        log.info("Timestamp tool called, returning: {}", timestamp);
        return "当前时间: " + timestamp;
    }
}