package dev.shaaf.keycloak.mcp.server.ingest.embedding;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

public class EmbeddingStoreProducer {


    @Produces
    @ApplicationScoped
    public EmbeddingStore<TextSegment> produceEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}

