package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long>, ChatMemoryRepository {
    @Override
    default List<String> findConversationIds() {
        return this.findAll().stream()
                .map(v -> v.getId().toString())
                .toList();
    }

    @Override
    default List<Message> findByConversationId(String conversationId) {
        return this.findById(Long.valueOf(conversationId))
                .orElseThrow()
                .getEntries()
                .stream()
                .map(ChatEntryEntity::toMessage)
                .toList();
    }

    @Override
    default void saveAll(String conversationId, List<Message> messages) {
        var chat = this.findById(Long.valueOf(conversationId))
                .orElseThrow();
        messages.forEach(m -> chat.addEntry(ChatEntryEntity.fromMessage(m)));
        save(chat);
    }

    @Override
    default void deleteByConversationId(String conversationId) {
        // not implemented
    }
}


