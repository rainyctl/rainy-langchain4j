package cc.rainyctl.rainylangchain4j.service;

import cc.rainyctl.rainylangchain4j.vendor.weatherbit.WeatherbitClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherbitClient weatherbitClient;

    public WeatherService(WeatherbitClient weatherbitClient) {
        this.weatherbitClient = weatherbitClient;
    }

    public JsonNode getWeather(String city) {
        String apiKey = System.getenv("WEATHERBIT_API_KEY");
        return weatherbitClient.getCurrent(city, apiKey);
    }
}
