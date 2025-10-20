package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PromptRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PromptRepository promptRepository;

    private PromptEntity testPrompt;

    @BeforeEach
    void setUp() {
        testPrompt = PromptEntity.builder()
                .type("RAG")
                .name("Test Prompt")
                .content("You are a helpful assistant")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_ShouldPersistPrompt() {
        // When
        PromptEntity saved = promptRepository.save(testPrompt);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Prompt");
        assertThat(saved.getType()).isEqualTo("RAG");
        assertThat(saved.getContent()).isEqualTo("You are a helpful assistant");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_WhenPromptExists_ShouldReturnPrompt() {
        // Given
        PromptEntity saved = entityManager.persistAndFlush(testPrompt);

        // When
        Optional<PromptEntity> found = promptRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Prompt");
        assertThat(found.get().getType()).isEqualTo("RAG");
    }

    @Test
    void findById_WhenPromptNotExists_ShouldReturnEmpty() {
        // When
        Optional<PromptEntity> found = promptRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllPrompts() {
        // Given
        PromptEntity prompt1 = PromptEntity.builder()
                .type("RAG")
                .name("Prompt 1")
                .content("Content 1")
                .createdAt(LocalDateTime.now())
                .build();
        PromptEntity prompt2 = PromptEntity.builder()
                .type("Expansion")
                .name("Prompt 2")
                .content("Content 2")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(prompt1);
        entityManager.persistAndFlush(prompt2);

        // When
        List<PromptEntity> allPrompts = promptRepository.findAll();

        // Then
        assertThat(allPrompts).hasSize(2);
        assertThat(allPrompts).extracting(PromptEntity::getName)
                .containsExactlyInAnyOrder("Prompt 1", "Prompt 2");
    }

    @Test
    void delete_ShouldRemovePrompt() {
        // Given
        PromptEntity saved = entityManager.persistAndFlush(testPrompt);
        Long promptId = saved.getId();

        // When
        promptRepository.deleteById(promptId);
        entityManager.flush();

        // Then
        Optional<PromptEntity> found = promptRepository.findById(promptId);
        assertThat(found).isEmpty();
    }
}
