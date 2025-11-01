package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.repository.ChatRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresMemory implements ChatMemory {
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void add(String conversationId, Message message) {
        var chat = chatRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        chat.addEntry(ChatEntryEntity.fromMessage(message));
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        messages.forEach(message -> this.add(conversationId, message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> get(String conversationId) {
        var chat = chatRepository.findById(Long.valueOf(conversationId)).orElseThrow();
        return chat.getEntries().stream()
                .map(ChatEntryEntity::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {

    }
}
