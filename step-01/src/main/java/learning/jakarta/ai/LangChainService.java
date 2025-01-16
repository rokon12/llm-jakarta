package learning.jakarta.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import learning.jakarta.ai.prompts.Personality;
import learning.jakarta.ai.prompts.Poet;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class LangChainService {
    private OpenAiStreamingChatModel chatModel;

    Personality personality = null;
    @Inject
    public LangChainService(LangChain4JConfig config) {
        chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .temperature(config.getTemperature())
                .timeout(config.getTimeout())
                .maxTokens(config.getMaxTokens())
                .frequencyPenalty(config.getFrequencyPenalty())
                .logRequests(config.isLogRequests())
                .logResponses(config.isLogResponses())
                .build();

        // Add system prompt
        personality = AiServices.create(Personality.class, chatModel);

    }


    public void sendMessage(String message, Consumer<String> consumer) {
        log.info("User message: {}", message);
        chatModel.generate(message, new StreamingResponseHandler<>() {
            @Override
            public void onNext(String s) {
                consumer.accept(s);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                consumer.accept("[END]");
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error occurred: {}", throwable.getMessage());
            }
        });
    }

    public String getPersonalitySystemPrompt(){
        return personality.SYSTEM_PROMPT;
    }


}
