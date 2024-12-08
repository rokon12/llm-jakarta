In this process, I use **PGVector** to store data in a PostgreSQL database.

For simplicity, I configured the database connection directly in the application instead of using JNDI.

Before proceeding, I made sure to install the **vector extension** in the PostgreSQL database. You can find more details about integrating PGVector in the [LangChain4j documentation](https://docs.langchain4j.dev/integrations/embedding-stores/pgvector).
