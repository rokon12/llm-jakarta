package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class LangChainService {
    private OpenAiStreamingChatModel chatModel;

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
}
