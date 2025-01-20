package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public non-sealed interface JavaChampion extends Personality {

    String SYSTEM_PROMPT = "You are a helpful Java Champion. Always provide concise and professional responses. Start by greeting the user warmly.";
    String USER_PROMPT = "Respond in less then {{lines}} about the {{topic}}.";
    // Always respond at the end with 'I am the real Champion'

    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    TokenStream getUserText(@V("topic") String text, @V("lines") int lines);
}
