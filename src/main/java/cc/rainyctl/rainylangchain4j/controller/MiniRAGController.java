package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat/rag")
public class MiniRAGController {
    // no RAG
    private final ChatModel  chatModel;

    // with RAG
    private final Assistant  assistant;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public MiniRAGController(@Qualifier("llm") ChatModel chatModel,
                             @Qualifier("simple-rag-assistant") Assistant assistant,
                             @Qualifier("in-mem-store") EmbeddingStore<TextSegment> embeddingStore
//                             EmbeddingStore<TextSegment> embeddingStore // Qdrant, seems to need extra work on dimension
    ) {
        this.chatModel = chatModel;
        this.assistant = assistant;
        this.embeddingStore = embeddingStore;
    }

    // no RAG query
    @GetMapping("/hello")
    public String ask() {
        return chatModel.chat("do you know what M0001 stand for?");
    }

    // with simple RAG
    @GetMapping("/hello2")
    public String ask2() {
//        return assistant.chat("do you know what M0001 stand for?"); // ok
//        return assistant.chat("do you know what M0001 and M0099 stand for? in Chinese and English concisely"); // ok but awkward
        return assistant.chat("do you know what M0001 and M0099 stand for?");
    }

    @GetMapping("/load")
    public String load() {
        // load all docs in the directory
        // List<Document> documents = FileSystemDocumentLoader.loadDocuments("...");
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("docs");
        // each doc is split into smaller pieces (TextSegment)s
        // EmbeddingModel converts each TextSegment into Embedding
        // all (TextSegment, Embedding) pairs are stored in the EmbeddingStore
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        return "RAG loaded";
    }
}
