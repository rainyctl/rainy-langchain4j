package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.config.QdrantProperties;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/embed")
public class EmbeddingModelController {

    private final EmbeddingModel embeddingModel;

    private final QdrantClient qdrantClient;

    private final EmbeddingStore<TextSegment>  embeddingStore;

    private final QdrantProperties  qdrantProperties;

    public EmbeddingModelController(EmbeddingModel embeddingModel, QdrantClient qdrantClient, EmbeddingStore<TextSegment> embeddingStore, QdrantProperties qdrantProperties) {
        this.embeddingModel = embeddingModel;
        this.qdrantClient = qdrantClient;
        this.embeddingStore = embeddingStore;
        this.qdrantProperties = qdrantProperties;
    }

    // create a collection in Qdrant
    @GetMapping("/create")
    public void createCollection() {
        var vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(embeddingModel.dimension())
                .build();
        qdrantClient.createCollectionAsync(qdrantProperties.getCollectionName(), vectorParams);
    }

    // inspect current embedding model's dimension
    @GetMapping("/info")
    public String info() {
        return "vector dimension: " + embeddingModel.dimension();
    }

    // save an embedded prompt
    @GetMapping("/add")
    public String add() {
        String prompt = """
                《静夜思》
                床前明月光，
                疑是地上霜。
                举头望明月，
                低头思故乡。
                """;

        TextSegment segment = TextSegment.from(prompt);
        segment.metadata().put("author", "李白");
        Embedding embedding = embeddingModel.embed(segment).content();
        log.info("got embedding of dimension: {}", embedding.dimension());
        return embeddingStore.add(embedding, segment); // need both, if segment is missing you won't get original text back
    }

    // save an embedded prompt
    @GetMapping("/add2")
    public String add2() {
        String prompt = """
                《春夜喜雨》
                好雨知时节，
                当春乃发生。
                随风潜入夜，
                润物细无声。
                野径云俱黑，
                江船火独明。
                晓看红湿处，
                花重锦官城。
                """;

        TextSegment segment = TextSegment.from(prompt);
        segment.metadata().put("author", "杜甫");
        Embedding embedding = embeddingModel.embed(segment).content();
        log.info("got embedding of dimension: {}", embedding.dimension());
        return embeddingStore.add(embedding, segment);
    }

    // search similar
    @GetMapping("/ask")
    public String ask() {
         Embedding queryEmbedding = embeddingModel.embed("李白写啥了").content(); // ok
//         Embedding queryEmbedding = embeddingModel.embed("思念家乡").content(); // ok?
//         Embedding queryEmbedding = embeddingModel.embed("《静夜思》").content(); // ok

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                // .filter(...)
                .maxResults(1)
                .build();

        EmbeddingSearchResult<TextSegment> res = embeddingStore.search(request);
        if (res.matches().isEmpty()) {
            return "nothing";
        }

        TextSegment segment = res.matches().get(0).embedded();
        if (segment == null) {
            return "null segment found";
        }
        log.info("found {} items", res.matches().size());
        log.info("search res: {}", res.matches().get(0).embedded());
        return res.matches().get(0).embedded().text();
    }
}
