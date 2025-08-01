package dev.shaaf.keycloak.mcp.server.ingest;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.shaaf.keycloak.mcp.server.ingest.loader.JarDocumentLoader;
import dev.shaaf.keycloak.mcp.server.ingest.parser.JsoupDocumentParser;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@ApplicationScoped
@Startup
public class DocumentIngestor {

    @Inject
    EmbeddingStore<TextSegment> store;
    @Inject
    EmbeddingModel embeddingModel;

    @PostConstruct
    void ingestDocuments() {
        try {
            List<Document> documents = new JarDocumentLoader().loadDocuments("docs", new JsoupDocumentParser());

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingStore(store)
                    .embeddingModel(embeddingModel)
                    .documentSplitter(recursive(500, 0))
                    .build();

            ingestor.ingest(documents);
            Log.info("Ingestion complete.");

        } catch (Exception e) {
            Log.error("Failed during document ingestion", e);
        }
    }


}