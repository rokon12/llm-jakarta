package ca.bazlur.workshop.jakarta.llm.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AppConfig {

    @Produces
    @ApplicationScoped
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Produces
    @ApplicationScoped
    public PgVectorEmbeddingStore produceEmbeddingStore(DatabaseProp databaseProp, EmbeddingModel embeddingModel) {
        return PgVectorEmbeddingStore.builder()
                .dropTableFirst(true)
                .createTable(true)
                .useIndex(true)
                .host(databaseProp.getHost())
                .port(databaseProp.getPort())
                .database(databaseProp.getDatabase())
                .user(databaseProp.getUser())
                .password(databaseProp.getPassword())
                .dimension(embeddingModel.dimension())
                .indexListSize(100)
                .table("embedding_store")
                .build();
    }
}

