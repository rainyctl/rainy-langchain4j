package cc.rainyctl.rainylangchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatPersistentAssistant {
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
