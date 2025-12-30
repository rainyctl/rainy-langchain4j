package cc.rainyctl.rainylangchain4j.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm.deepseek")
@Data
public class DeepSeekProperties {
    private String baseUrl;
    private String apiKey;
    private String modelName;
}
