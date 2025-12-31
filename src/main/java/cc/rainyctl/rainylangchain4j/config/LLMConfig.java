package cc.rainyctl.rainylangchain4j.config;

import cc.rainyctl.rainylangchain4j.listener.MyChatModelListener;
import cc.rainyctl.rainylangchain4j.memory.RedisChatMemoryStore;
import cc.rainyctl.rainylangchain4j.service.AssistantWithMemory;
import cc.rainyctl.rainylangchain4j.service.ChatPersistentAssistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean
    @Primary
    public AssistantWithMemory assistantWithMemory(@Qualifier("openAI") ChatModel chatModel) {
        return AiServices.builder(AssistantWithMemory.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean("mem2")
    public AssistantWithMemory assistantWithTokenMemory(@Qualifier("openAI") ChatModel chatModel) {
        var estimator = new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_5);
        return AiServices.builder(AssistantWithMemory.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> TokenWindowChatMemory.withMaxTokens(1000, estimator))
                .build();
    }

    // alternatively, if you use @AiService
    @Bean("chatMemoryProvider")
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(100);
    }

    @Bean
    public ChatPersistentAssistant  chatPersistentAssistant(@Qualifier("llm") ChatModel chatModel, RedisChatMemoryStore redisChatMemoryStore) {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(1000)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
        return AiServices.builder(ChatPersistentAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }
}
