package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<ChatEntity> findAll() {
        return chatRepository.findAll();
    }

    public Optional<ChatEntity> findById(Long id) {
        return chatRepository.findById(id);
    }

    public ChatEntity create(ChatEntity chat) {
        chat.setId(null);
        return chatRepository.save(chat);
    }

    public ChatEntity update(Long id, ChatEntity updated) {
        return chatRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    return chatRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + id));
    }

    public void delete(Long id) {
        chatRepository.deleteById(id);
    }
}


