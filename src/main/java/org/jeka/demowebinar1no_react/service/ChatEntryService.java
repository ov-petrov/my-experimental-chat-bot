package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.repository.ChatEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatEntryService {

    private final ChatEntryRepository chatEntryRepository;

    public List<ChatEntryEntity> findByChat(ChatEntity chat) {
        return chatEntryRepository.findByChatOrderByTimestampAsc(chat);
    }

    public Optional<ChatEntryEntity> findById(Long id) {
        return chatEntryRepository.findById(id);
    }

    public ChatEntryEntity create(ChatEntryEntity entry) {
        entry.setId(null);
        return chatEntryRepository.save(entry);
    }

    public ChatEntryEntity update(Long id, ChatEntryEntity updated) {
        return chatEntryRepository.findById(id)
                .map(existing -> {
                    existing.setRole(updated.getRole());
                    existing.setContent(updated.getContent());
                    return chatEntryRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Chat entry not found: " + id));
    }

    public void delete(Long id) {
        chatEntryRepository.deleteById(id);
    }
}


