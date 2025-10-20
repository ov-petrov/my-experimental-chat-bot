package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.repository.PromptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    public List<PromptEntity> findAll() {
        return promptRepository.findAll();
    }

    public Optional<PromptEntity> findById(Long id) {
        return promptRepository.findById(id);
    }

    public PromptEntity create(PromptEntity prompt) {
        prompt.setId(null);
        return promptRepository.save(prompt);
    }

    public PromptEntity update(Long id, PromptEntity updated) {
        return promptRepository.findById(id)
                .map(existing -> {
                    existing.setType(updated.getType());
                    existing.setName(updated.getName());
                    existing.setContent(updated.getContent());
                    return promptRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Prompt not found: " + id));
    }

    public void delete(Long id) {
        promptRepository.deleteById(id);
    }

    /**
     * Find prompts by type (e.g., "System", "RAG", "Expansion")
     */
    public List<PromptEntity> findByType(String type) {
        return promptRepository.findByType(type);
    }

    /**
     * Find the first prompt by type (useful for system prompts)
     */
    public Optional<PromptEntity> findFirstByType(String type) {
        return promptRepository.findFirstByTypeOrderByCreatedAtDesc(type);
    }

    /**
     * Find prompts by name (case-insensitive)
     */
    public List<PromptEntity> findByNameContainingIgnoreCase(String name) {
        return promptRepository.findByNameContainingIgnoreCase(name);
    }
}


