package learning.jakarta.ai.prompts;

import dev.langchain4j.service.TokenStream;

public sealed interface Personality permits JavaChampion, Poet, ChainOfThought {
    TokenStream getUserText(String text);
}
