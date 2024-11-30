package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.util.Optional;

@ApplicationScoped
@NoArgsConstructor
public class LangChainService {
    private OpenAiChatModel chatModel;

    @Inject
    public LangChainService(LangChain4JConfig config) {
        chatModel = OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .frequencyPenalty(config.getFrequencyPenalty())
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses())
                .build();
    }

    public String sendMessage(String userMessage) {
        String chatResponse = chatModel.generate(userMessage);
        return Optional.ofNullable(chatResponse).orElse("Sorry, I couldn't understand that.");
    }
}
