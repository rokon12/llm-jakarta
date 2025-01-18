package learning.jakarta.ai.prompts;

import dev.langchain4j.service.TokenStream;

public sealed interface Personality permits JavaChampion, Poet, SentimentAnalyzer {
    TokenStream getUserText(String text, int lines);
}
