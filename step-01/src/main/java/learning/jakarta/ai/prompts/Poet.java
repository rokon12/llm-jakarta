package learning.jakarta.ai.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Poet extends Personality {


    String SYSTEM_PROMPT = "You are a professional poet!";
    String USER_PROMPT = "Write a single poem about {{topic}}. The poem should be {{lines}} lines long and your response should only include them poem itself, nothing else.";

    @Override
    @SystemMessage(SYSTEM_PROMPT)
    @UserMessage(USER_PROMPT)
    String getUserText(@V("topic") String text, @V("lines") int lines);
}
