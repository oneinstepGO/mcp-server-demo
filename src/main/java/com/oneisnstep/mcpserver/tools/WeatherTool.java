package com.oneisnstep.mcpserver.tools;

import com.oneisnstep.mcpserver.annotation.ToolBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
@ToolBean
public class WeatherTool {

    private final RestClient restClient;

    public WeatherTool() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.weather.gov")
                .defaultHeader("Accept", "application/geo+json")
                .defaultHeader("User-Agent", "WeatherApiClient/1.0 (your@email.com)")
                .build();
    }

    /**
     * 根据经纬度获取天气预报
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 天气预报
     */
    @Tool(name = "getWeatherForecastByLocation", description = "Get weather forecast for a specific latitude/longitude")
    public String getWeatherForecastByLocation(
            @ToolParam(description = "Latitude coordinate") double latitude,
            @ToolParam(description = "Longitude coordinate") double longitude) {
        try {
            log.info("Weather forecast tool called with lat={}, lon={}", latitude, longitude);
            // 首先获取点位信息
            String pointsUrl = String.format("/points/%.4f,%.4f", latitude, longitude);
            Map<String, Object> pointsResponse = restClient.get()
                    .uri(pointsUrl)
                    .retrieve()
                    .body(Map.class);

            if (pointsResponse == null || !pointsResponse.containsKey("properties")) {
                return "无法获取位置信息";
            }

            // 从点位信息中获取预报URL
            @SuppressWarnings("unchecked")
            Map<String, Object> properties = (Map<String, Object>) pointsResponse.get("properties");
            String forecastUrl = (String) properties.get("forecast");

            if (forecastUrl == null) {
                return "无法获取预报URL";
            }

            // 获取预报信息
            Map<String, Object> forecastResponse = restClient.get()
                    .uri(forecastUrl)
                    .retrieve()
                    .body(Map.class);

            if (forecastResponse == null || !forecastResponse.containsKey("properties")) {
                return "无法获取预报信息";
            }

            // 解析预报数据
            @SuppressWarnings("unchecked")
            Map<String, Object> forecastProperties = (Map<String, Object>) forecastResponse.get("properties");
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> periods = (java.util.List<Map<String, Object>>) forecastProperties
                    .get("periods");

            if (periods == null || periods.isEmpty()) {
                return "无预报数据可用";
            }

            // 构建预报信息
            StringBuilder forecast = new StringBuilder();
            forecast.append(String.format("位置: 纬度 %.4f, 经度 %.4f\n", latitude, longitude));

            for (int i = 0; i < Math.min(3, periods.size()); i++) {
                Map<String, Object> period = periods.get(i);
                forecast.append("\n时段: ").append(period.get("name")).append("\n");
                forecast.append("温度: ").append(period.get("temperature")).append(" ")
                        .append(period.get("temperatureUnit")).append("\n");
                forecast.append("风向: ").append(period.get("windDirection")).append(", 风速: ")
                        .append(period.get("windSpeed")).append("\n");
                forecast.append("预报: ").append(period.get("detailedForecast")).append("\n");
            }

            return forecast.toString();
        } catch (Exception e) {
            log.error("获取天气预报时发生错误", e);
            return "获取天气预报时发生错误: " + e.getMessage();
        }
    }

    @Tool(name = "getAlerts", description = "Get weather alerts for a US state.只能查询美国的天气。")
    public String getAlerts(@ToolParam(description = "Two-letter US state code (e.g. CA, NY)") String state) {
        try {
            log.info("Weather alerts tool called with state={}", state);
            // 获取指定州的天气警报
            String alertsUrl = String.format("/alerts/active/area/%s", state);
            Map<String, Object> alertsResponse = restClient.get()
                    .uri(alertsUrl)
                    .retrieve()
                    .body(Map.class);

            if (alertsResponse == null || !alertsResponse.containsKey("features")) {
                return "无法获取警报信息";
            }

            // 解析警报数据
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> features = (java.util.List<Map<String, Object>>) alertsResponse
                    .get("features");

            if (features == null || features.isEmpty()) {
                return String.format("%s 州目前没有活跃的天气警报", state);
            }

            // 构建警报信息
            StringBuilder alerts = new StringBuilder();
            alerts.append(String.format("%s 州的天气警报:\n", state));

            for (Map<String, Object> feature : features) {
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) feature.get("properties");

                if (properties != null) {
                    alerts.append("\n警报类型: ").append(properties.get("event")).append("\n");
                    alerts.append("严重程度: ").append(properties.get("severity")).append("\n");
                    alerts.append("影响区域: ").append(properties.get("areaDesc")).append("\n");
                    alerts.append("描述: ").append(properties.get("description")).append("\n");

                    if (properties.containsKey("instruction") && properties.get("instruction") != null) {
                        alerts.append("安全指示: ").append(properties.get("instruction")).append("\n");
                    }
                }
            }

            return alerts.toString();
        } catch (Exception e) {
            log.error("获取天气警报时发生错误", e);
            return "获取天气警报时发生错误: " + e.getMessage();
        }
    }
}
