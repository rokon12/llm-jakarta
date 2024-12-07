package ca.bazlur.workshop.jakarta.llm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ChatMessageRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(ChatMessageEntity chatMessage) {
        entityManager.persist(chatMessage);
    }

    public List<ChatMessageEntity> findByMemoryId(String memoryId) {
        return entityManager.createQuery("SELECT c FROM ChatMessageEntity c WHERE c.memoryId = :memoryId", ChatMessageEntity.class)
                .setParameter("memoryId", memoryId)
                .getResultList();
    }

    @Transactional
    public void deleteByMemoryId(String memoryId) {
        entityManager.createQuery("DELETE FROM ChatMessageEntity c WHERE c.memoryId = :memoryId")
                .setParameter("memoryId", memoryId)
                .executeUpdate();
    }

}
