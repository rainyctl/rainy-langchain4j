package cc.rainyctl.rainylangchain4j.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

// high-level api
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "llm")
public interface MagicianChatService {
    @SystemMessage("你是一个三十岁的魔法师")
    String chat(String userMessage);
}
