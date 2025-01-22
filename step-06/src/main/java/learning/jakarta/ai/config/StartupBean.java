package learning.jakarta.ai.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Slf4j
@Startup
@Singleton
public class StartupBean {

    @Inject
    private ConfigProp config;
    @Inject
    private EmbeddingModel embeddingModel;
    @Inject
    private PgVectorEmbeddingStore pgVectorStore;

    @PostConstruct
    public void init() {
        log.info("Application started successfully.");

        List<Document> documents = FileSystemDocumentLoader.loadDocuments(config.getDocumentsDir(), new ApacheTikaDocumentParser());
        log.info("Total documents parsed: {}", documents.size());

        DocumentByParagraphSplitter paragraphSplitter = new DocumentByParagraphSplitter(config.getMaxSegmentSizeInTokens(), config.getMaxOverlapSizeInTokens());

        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(paragraphSplitter::split)
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()))
                .embeddingModel(embeddingModel)
                .embeddingStore(pgVectorStore)
                .build();

        embeddingStoreIngestor.ingest(documents);
    }
}