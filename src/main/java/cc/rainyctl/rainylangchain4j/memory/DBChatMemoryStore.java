package cc.rainyctl.rainylangchain4j.memory;

import cc.rainyctl.rainylangchain4j.mapper.ChatMemoryMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBChatMemoryStore implements ChatMemoryStore {
    private static final String MEMORY_PREFIX = "memory:";

    private final ChatMemoryMapper chatMemoryMapper;

    public DBChatMemoryStore(ChatMemoryMapper chatMemoryMapper) {
        this.chatMemoryMapper = chatMemoryMapper;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String res = chatMemoryMapper.findByMemoryId(MEMORY_PREFIX + memoryId);
        return ChatMessageDeserializer.messagesFromJson(res);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
//        chatMemoryMapper.updateMemory(MEMORY_PREFIX + memoryId, ChatMessageSerializer.messagesToJson(messages));
        chatMemoryMapper.upsertMemory(MEMORY_PREFIX + memoryId, ChatMessageSerializer.messagesToJson(messages));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatMemoryMapper.deleteMemory(MEMORY_PREFIX + memoryId);
    }
}
