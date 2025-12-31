package cc.rainyctl.rainylangchain4j.service;

import cc.rainyctl.rainylangchain4j.prompt.CoolPrompt;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "llm", chatMemoryProvider = "chatMemoryProvider")
public interface CoolAssistant2 {
    String chat(CoolPrompt prompt);
}
