package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.MagicianChatService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat")
public class OpenAIController {
    private final ChatModel openAIChatModel;

    private final ChatModel deepSeekChatModel;

    // high-level api
    private final MagicianChatService assistant;

    public OpenAIController(@Qualifier("openAI") ChatModel openAIChatModel, @Qualifier("deepseek") ChatModel deepSeekChatModel, MagicianChatService assistant) {
        this.openAIChatModel = openAIChatModel;
        this.deepSeekChatModel = deepSeekChatModel;
        this.assistant = assistant;
    }


    // simple text message
    // OpenAI model
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "message", defaultValue = "hello, introduce yourself") String message) {
        return openAIChatModel.chat(message);
    }

    // simple text message with detailed response including token usage, etc
    // OpenAI model
    @GetMapping("/hello2")
    public String hello2(@RequestParam(value = "message", defaultValue = "hello, introduce yourself") String message) {
        ChatMessage msg = UserMessage.from(message);
        ChatResponse resp = openAIChatModel.chat(msg);
        TokenUsage tokenUsage = resp.tokenUsage();
        log.info("token usage: {}", tokenUsage);
        return resp.aiMessage().text();
    }

    // simple text message
    // DeepSeek model
    @GetMapping("/d/hello")
    public String helloDeepSeek(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return deepSeekChatModel.chat(message);
    }

    // simple text message with detailed response including token usage, etc
    // DeepSeek model
    @GetMapping("/d/hello2")
    public String helloDeepSeek2(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        ChatMessage msg = UserMessage.from(message);
        ChatResponse resp = deepSeekChatModel.chat(msg);
        TokenUsage tokenUsage = resp.tokenUsage();
        log.info("token usage: {}", tokenUsage);
        return resp.aiMessage().text();
    }

    @GetMapping("/hello3")
    public String hello3(@RequestParam(value = "message", defaultValue = "你是谁") String message) {
        return assistant.chat(message);
    }
}
