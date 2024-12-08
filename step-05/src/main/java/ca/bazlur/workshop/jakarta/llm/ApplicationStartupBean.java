package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Singleton
@Startup
public class ApplicationStartupBean {

    @Inject
    PgVectorEmbeddingStore pgVectorStore;

    @Inject
    EmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        log.info("Application started successfully.");

        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/Users/bazlur/playground/llm-jakarta/step-04/documents", new ApacheTikaDocumentParser());

        log.info("Total documents parsed: {}", documents.size());

        documents.forEach(document -> {
            TextSegment textSegment = document.toTextSegment();
            Embedding content = embeddingModel.embed(textSegment).content();
            pgVectorStore.add(content, textSegment);
        });
    }
}

