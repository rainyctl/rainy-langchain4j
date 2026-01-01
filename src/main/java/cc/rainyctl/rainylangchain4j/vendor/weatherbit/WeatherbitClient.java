package cc.rainyctl.rainylangchain4j.vendor.weatherbit;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherbit", url = "https://api.weatherbit.io/v2.0")
public interface WeatherbitClient {
    @GetMapping("/current")
    JsonNode getCurrent(@RequestParam("city") String city, @RequestParam("key") String apiKey);
}
