package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.prompt.CoolPrompt;
import cc.rainyctl.rainylangchain4j.service.CoolAssistant;
import cc.rainyctl.rainylangchain4j.service.CoolAssistant2;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class PromptEngController {

    private final CoolAssistant coolAssistant;

    private final CoolAssistant2  coolAssistant2;

    private final ChatModel  chatModel;

    public PromptEngController(CoolAssistant coolAssistant, CoolAssistant2 coolAssistant2, @Qualifier("llm") ChatModel chatModel) {
        this.coolAssistant = coolAssistant;
        this.coolAssistant2 = coolAssistant2;
        this.chatModel = chatModel;
    }

    @GetMapping("/cool/hello")
    public String chat(@RequestParam(value = "message", defaultValue = "今天吃什么") String message, @RequestParam(value = "length", defaultValue = "100") int length) {
        return coolAssistant.chat(message, length);
    }

    @GetMapping("/cool/hello2")
    public String chat2(@RequestParam(value = "message", defaultValue = "今天吃什么") String message, @RequestParam(value = "tone", defaultValue = "古风") String tone) {
        val coolPrompt = new CoolPrompt();
        coolPrompt.setTone(tone);
        coolPrompt.setQuestion(message);
        return coolAssistant2.chat(coolPrompt);
    }

    @GetMapping("/cool/hello3")
    public String chat3(@RequestParam(value = "message", defaultValue = "今天吃什么") String message, @RequestParam(value = "tone", defaultValue = "简洁") String tone) {
        PromptTemplate template = PromptTemplate.from("你是一个 {{it}} 语气的助手， 回答一下 {{question}}");
        Prompt prompt = template.apply(Map.of("it", tone, "question", message));
        var res = chatModel.chat(prompt.toUserMessage());
        return res.aiMessage().text();
    }
}
