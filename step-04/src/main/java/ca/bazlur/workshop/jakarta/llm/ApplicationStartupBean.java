package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Singleton
@Startup
public class ApplicationStartupBean {

    private InMemoryEmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    public void init() {
        log.info("Application started successfully.");
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/Users/bazlur/playground/llm-jakarta/step-04/documents");
        embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
    }


    @Produces
    @ApplicationScoped
    public InMemoryEmbeddingStore<TextSegment> produceEmbeddingStore() {
        return embeddingStore;
    }
}
