package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface SentimentAnalyzer extends Personality {

    String SYSTEM_PROMPT = "You are a helpful Agent that analyzes user sentiments. ";
    String USER_PROMPT = "Analyze sentiment {{topic}}. You will only respond with positive and motivational quote and never without it! The quote should be {{lines}} lines long";

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String text, @V("lines") int lines);
}
