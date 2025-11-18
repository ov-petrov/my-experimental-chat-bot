package org.jeka.demowebinar1no_react.service;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.repository.ChatRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

@Slf4j
@Builder
public class PostgresMemory implements ChatMemory {
    private final ChatRepository chatMemoryRepository;
    private final int maxMessages;

    @Override
    public void add(String conversationId, Message message) {
        var chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        chat.addEntry(ChatEntryEntity.fromMessage(message));
        chatMemoryRepository.save(chat);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        messages.forEach(message -> this.add(conversationId, message));
    }

    @Override
    public List<Message> get(String conversationId) {
        var chat = chatMemoryRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        var skipMessages = Math.max(0, chat.getEntries().size() - maxMessages);
        return chat.getEntries().stream()
                .skip(skipMessages)
                .map(ChatEntryEntity::toMessage)
                .limit(maxMessages)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        // not implemented
    }
}
