package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class LangChainService {
    private final OpenAiChatModel chatModel;

    public LangChainService() {
        chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .build();
    }

    public String sendMessage(String userMessage) {
        String chatResponse = chatModel.generate(userMessage);
        return Optional.ofNullable(chatResponse).orElse("Sorry, I couldn't understand that.");
    }
}
