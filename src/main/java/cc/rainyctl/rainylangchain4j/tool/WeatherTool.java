package cc.rainyctl.rainylangchain4j.tool;

import cc.rainyctl.rainylangchain4j.service.WeatherService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeatherTool {
    private final WeatherService  weatherService;

    public WeatherTool(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Tool("get current weather of a city")
    public String getWeather(@P("city") String city) {
        log.info("tools for getWeather city:{}", city);
        return weatherService.getWeather(city).toString();
    }
}
