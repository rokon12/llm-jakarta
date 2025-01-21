package learning.jakarta.ai.prompts;

import dev.langchain4j.service.TokenStream;

public sealed interface Personality permits ChainOfThought, JavaChampion, MovieSummarizer, Poet {
    TokenStream getUserText(String text);
}
