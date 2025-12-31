package cc.rainyctl.rainylangchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface AssistantWithMemory {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
