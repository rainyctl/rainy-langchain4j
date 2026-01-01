package cc.rainyctl.rainylangchain4j.config;

import cc.rainyctl.rainylangchain4j.listener.MyChatModelListener;
import cc.rainyctl.rainylangchain4j.memory.DBChatMemoryStore;
import cc.rainyctl.rainylangchain4j.memory.RedisChatMemoryStore;
import cc.rainyctl.rainylangchain4j.service.*;
import cc.rainyctl.rainylangchain4j.tool.WeatherTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

@Slf4j
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
    @Primary
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

    @Bean("ps2")
    public ChatPersistentAssistant  chatPersistentAssistant2(@Qualifier("llm") ChatModel chatModel, DBChatMemoryStore dbChatMemoryStore) {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(1000)
                .chatMemoryStore(dbChatMemoryStore)
                .build();
        return AiServices.builder(ChatPersistentAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean("test_only")
    public FunctionAssistant weatherAssistant(@Qualifier("llm") ChatModel chatModel) {
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("get_current_weather")
                .description("tools for getting current weather of a city")
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("city", "the city of interest")
                        .required("city")
                        .build())
                .build();
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            log.info("[Tool] Getting current weather with args: {}", toolExecutionRequest.arguments());
            return "rainy now";
        };
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
    }

    @Bean("good_one")
    public FunctionAssistant weatherAssistant2(@Qualifier("llm") ChatModel chatModel, WeatherService weatherService) {
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("get_current_weather")
                .description("tools for getting current weather of a city")
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("city", "the city of interest")
                        .required("city")
                        .build())
                .build();
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            log.info("[Tool] Getting current weather with args: {}", toolExecutionRequest.arguments());
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> args = mapper.readValue(toolExecutionRequest.arguments(), new TypeReference<>() {
                });
                String city = (String) args.get("city");
                var res = weatherService.getWeather(city);
                return res.toString();
            } catch (JsonProcessingException e) {
                return "Failed because of " + e;
            }
        };
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
    }

    @Bean("fine")
    FunctionAssistant functionAssistant3(
            @Qualifier("llm") ChatModel chatModel,
            WeatherTool weatherTool
    ) {
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(weatherTool)
                .build();

    }

    @Bean
    EmbeddingModel embeddingModel(OpenAIProperties  openAIProperties) {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAIProperties.getApiKey())
                .baseUrl(openAIProperties.getBaseUrl())
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();
    }

    @Bean
    @Primary
    EmbeddingStore<TextSegment>  embeddingStore(QdrantProperties props) {
        return QdrantEmbeddingStore.builder()
                .host(props.getHost())
                .port(props.getPort())
                .collectionName(props.getCollectionName())
                .build();
    }

    @Bean
    QdrantClient qdrantClient(QdrantProperties props) {
        var builder = QdrantGrpcClient.newBuilder(props.getHost(), props.getPort(), false);
        return new QdrantClient(builder.build());
    }

    @Bean("in-mem-store")
    EmbeddingStore<TextSegment> inMemoryStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean("simple-rag-assistant")
    public Assistant assistant(
            @Qualifier("llm") ChatModel chatModel,
            @Qualifier("in-mem-store") EmbeddingStore<TextSegment> embeddingStore
//            EmbeddingStore<TextSegment> embeddingStore // Qdrant, seems to need extra work on dimension
    ) {
        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }

    @Bean
    public MCPAssistant assistantWithMCP(@Qualifier("llm") ChatModel chatModel) {
        // add time mcp, require uv which will then call uvx, no need to install server manually
        // https://github.com/modelcontextprotocol/servers/tree/main/src/time
        // https://mcp.so/server/time/modelcontextprotocol
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("uvx", "mcp-server-time"))
                // .environment(...)
                .logEvents(true)
                .build(); // should we close it at some point?
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
        ToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient) // can add multiple clients for different tasks
                .build();

        return AiServices.builder(MCPAssistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                .toolProvider(toolProvider)
                .build();
    }
}
