package cc.rainyctl.rainylangchain4j.config;

import cc.rainyctl.rainylangchain4j.listener.MyChatModelListener;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class LLMConfig {

    @Bean("openAI")
    public ChatModel openAI(OpenAIProperties props) {
        return OpenAiChatModel.builder()
                .apiKey(props.getApiKey())
                .baseUrl(props.getBaseUrl())
                .modelName(props.getModelName())
                .build();
    }

    @Bean("deepseek")
    public ChatModel deepseek(DeepSeekProperties props) {
        return OpenAiChatModel.builder()
                .apiKey(props.getApiKey())
                .baseUrl(props.getBaseUrl())
                .modelName(props.getModelName())
                .build();
    }

    @Bean("llm")
    @Primary
    public ChatModel defaultChatModel(DeepSeekProperties props) {
        return OpenAiChatModel.builder()
                .apiKey(props.getApiKey())
                .baseUrl(props.getBaseUrl())
                .modelName(props.getModelName())
                .logRequests(true)
                .logResponses(true)
                .maxRetries(2) // at most 3 requests will be sent
                .listeners(List.of(new MyChatModelListener()))
                // .timeout(Duration.ofSeconds(1)) timeout will trigger retry
                .build();
    }

    @Bean
    public StreamingChatModel streamingChatModel(OpenAIProperties props) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(props.getApiKey())
                .baseUrl(props.getBaseUrl())
                .modelName(props.getModelName())
                .build();
    }

    @Bean
    public ImageModel imageModel(OpenAIProperties props) {
        return OpenAiImageModel.builder()
                .apiKey(props.getApiKey())
                .modelName(OpenAiImageModelName.DALL_E_3)
                .build();
    }
}
