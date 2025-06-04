package com.oneisnstep.mcpserver.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oneisnstep.mcpserver.annotation.ToolBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
@ToolBean
public class JsonTool {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON字符串转换为带Lombok注解的Java类代码
     */
    @Tool(name = "jsonToJavaClass", description = "将JSON字符串转换为带Lombok注解的Java类代码")
    public String jsonToJavaClass(
            @ToolParam(description = "JSON字符串") String json,
            @ToolParam(description = "Java类名") String className) {
        try {
            JsonNode root = objectMapper.readTree(json);
            StringBuilder sb = new StringBuilder();
            sb.append("import lombok.Data;\n");
            sb.append("@Data\n");
            sb.append("public class ").append(className).append(" {\n");
            if (root.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String fieldName = entry.getKey();
                    JsonNode value = entry.getValue();
                    String type = inferType(value);
                    sb.append("    private ").append(type).append(" ").append(fieldName).append(";\n");
                }
            }
            sb.append("}\n");
            return sb.toString();
        } catch (Exception e) {
            log.error("jsonToJavaClass error", e);
            return "转换失败: " + e.getMessage();
        }
    }

    /**
     * 根据Java类定义，输出一个类型规范的示例json
     */
    @Tool(name = "javaClassToJson", description = "根据Java类定义，输出一个类型规范的示例json")
    public String javaClassToJson(@ToolParam(description = "Java类定义代码") String javaClassCode) {
        try {
            // 简单正则解析字段类型和名称
            String[] lines = javaClassCode.split("\\n");
            ObjectNode node = objectMapper.createObjectNode();
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("private ")) {
                    String[] parts = line.replace(";", "").split(" ");
                    if (parts.length >= 3) {
                        String type = parts[1];
                        String name = parts[2];
                        node.set(name, exampleValue(type));
                    }
                }
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            log.error("javaClassToJson error", e);
            return "转换失败: " + e.getMessage();
        }
    }

    // 类型推断
    private String inferType(JsonNode value) {
        if (value.isInt())
            return "int";
        if (value.isLong())
            return "long";
        if (value.isDouble() || value.isFloat() || value.isBigDecimal())
            return "double";
        if (value.isBoolean())
            return "boolean";
        if (value.isArray())
            return "java.util.List<Object>";
        if (value.isObject())
            return "Object";
        return "String";
    }

    // 示例值生成
    private JsonNode exampleValue(String type) {
        switch (type) {
            case "int":
                return objectMapper.getNodeFactory().numberNode(123);
            case "long":
                return objectMapper.getNodeFactory().numberNode(123456789L);
            case "double":
                return objectMapper.getNodeFactory().numberNode(3.14);
            case "boolean":
                return objectMapper.getNodeFactory().booleanNode(true);
            case "java.util.List<Object>":
                return objectMapper.createArrayNode().add("item1").add("item2");
            case "Object":
                return objectMapper.createObjectNode().put("key", "value");
            default:
                return objectMapper.getNodeFactory().textNode("示例文本");
        }
    }
}
