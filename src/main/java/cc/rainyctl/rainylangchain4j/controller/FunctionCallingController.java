package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.FunctionAssistant;
import cc.rainyctl.rainylangchain4j.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat")
public class FunctionCallingController {

    private final WeatherService weatherService;

    private final FunctionAssistant  assistant;

    private final FunctionAssistant assistant2;

    public FunctionCallingController(
            WeatherService weatherService,
            @Qualifier("test_only") FunctionAssistant assistant,
            @Qualifier("good_one") FunctionAssistant assistant2) {
        this.weatherService = weatherService;
        this.assistant = assistant;
        this.assistant2 = assistant2;
    }

    @GetMapping("/fn/current")
    public JsonNode getWeather() {
        var res = weatherService.getWeather("Palo Alto");
        log.info("Current weather is {}", res);
        return res;
    }

    // test mock response
    // 1) no city -> ask for city
    // 2) have city -> say it's rainy
    @GetMapping("/fn/test")
    public String test(@RequestParam(value = "city", defaultValue = "") String city) {
        return assistant.chat("how's the weather now in " + city);
    }

    @GetMapping("/fn/good")
    public String test2(@RequestParam(value = "city", defaultValue = "") String city) {
        return assistant2.chat("how's the weather now in " + city);
    }
}
