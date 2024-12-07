package ca.bazlur.workshop.jakarta.llm;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


import static ca.bazlur.workshop.jakarta.llm.utils.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@NoArgsConstructor
@ApplicationScoped
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Inject
    private ChatMessageRepository repository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return repository.findByMemoryId(memoryId.toString()).stream()
                .map(entity -> messagesFromJson(entity.getMessage()))
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String messagesToJson = messagesToJson(messages);

        messages.forEach(message -> {
            ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                    .memoryId(memoryId.toString())
                    .message(messagesToJson)
                    .type(message.type())
                    .createdDate(Instant.now())
                    .updatedDate(Instant.now())
                    .build();
            repository.save(chatMessageEntity);
        });
    }

    @Override
    public void deleteMessages(Object memoryId) {
        repository.deleteByMemoryId(memoryId.toString());
    }
}
