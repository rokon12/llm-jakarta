package ca.bazlur.workshop.jakarta.llm.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Slf4j
@ApplicationScoped
@DataSourceDefinition(
        name = "java:jboss/datasources/LLMJakartaPostgresDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "llmjakarta",
        password = "llmjakarta",
        url = "jdbc:postgresql://localhost:5432/llmjakarta"
)
public class AppConfig {
    @Produces
    @ApplicationScoped
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Produces
    @ApplicationScoped
    public PgVectorEmbeddingStore produceEmbeddingStoreUsingDataSource(EmbeddingModel embeddingModel) {

        try {
            InitialContext ic = new InitialContext();
            DataSource dataSource = (DataSource) ic.lookup("java:jboss/datasources/LLMJakartaPostgresDS");
            log.info("Creating PgVectorEmbeddingStore using datasource: {}", dataSource);

            return PgVectorEmbeddingStore.datasourceBuilder()
                    .datasource(dataSource)
                    .dropTableFirst(true)
                    .createTable(true)
                    .useIndex(true)
                    .dimension(embeddingModel.dimension())
                    .indexListSize(100)
                    .table("embedding_store")
                    .build();

        } catch (NamingException e) {
            log.error("Error looking up DataSource", e);
            throw new RuntimeException("Error looking up DataSource", e);
        }
    }
}

