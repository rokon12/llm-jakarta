package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface ChainOfThought extends Personality {

    String SYSTEM_PROMPT = "You are a helpful Java Champion. Always provide concise and professional responses. When solving problems, always explain your reasoning step-by-step before providing the final answer.";
    String USER_PROMPT = "Answer about this {{topic}}";

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String text);
}

// Example prompts
// How do I implement a binary search algorithm in Java?
