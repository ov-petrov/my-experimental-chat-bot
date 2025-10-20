package org.jeka.demowebinar1no_react.service;

import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.repository.PromptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptServiceTest {

    @Mock
    private PromptRepository promptRepository;

    @InjectMocks
    private PromptService promptService;

    private PromptEntity testPrompt;

    @BeforeEach
    void setUp() {
        testPrompt = PromptEntity.builder()
                .id(1L)
                .type("RAG")
                .name("Test Prompt")
                .content("You are a helpful assistant")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnAllPrompts() {
        // Given
        List<PromptEntity> expectedPrompts = Arrays.asList(testPrompt);
        when(promptRepository.findAll()).thenReturn(expectedPrompts);

        // When
        List<PromptEntity> result = promptService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Prompt");
        verify(promptRepository).findAll();
    }

    @Test
    void findById_WhenPromptExists_ShouldReturnPrompt() {
        // Given
        when(promptRepository.findById(1L)).thenReturn(Optional.of(testPrompt));

        // When
        Optional<PromptEntity> result = promptService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Prompt");
        verify(promptRepository).findById(1L);
    }

    @Test
    void findById_WhenPromptNotExists_ShouldReturnEmpty() {
        // Given
        when(promptRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<PromptEntity> result = promptService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(promptRepository).findById(999L);
    }

    @Test
    void create_ShouldSaveAndReturnPrompt() {
        // Given
        PromptEntity newPrompt = PromptEntity.builder()
                .type("RAG")
                .name("New Prompt")
                .content("New content")
                .build();
        when(promptRepository.save(any(PromptEntity.class))).thenReturn(testPrompt);

        // When
        PromptEntity result = promptService.create(newPrompt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Prompt");
        verify(promptRepository).save(any(PromptEntity.class));
    }

    @Test
    void update_WhenPromptExists_ShouldUpdateAndReturnPrompt() {
        // Given
        PromptEntity updatedPrompt = PromptEntity.builder()
                .type("Expansion")
                .name("Updated Prompt")
                .content("Updated content")
                .build();
        when(promptRepository.findById(1L)).thenReturn(Optional.of(testPrompt));
        when(promptRepository.save(any(PromptEntity.class))).thenReturn(updatedPrompt);

        // When
        PromptEntity result = promptService.update(1L, updatedPrompt);

        // Then
        assertThat(result.getName()).isEqualTo("Updated Prompt");
        verify(promptRepository).findById(1L);
        verify(promptRepository).save(any(PromptEntity.class));
    }

    @Test
    void update_WhenPromptNotExists_ShouldThrowException() {
        // Given
        PromptEntity updatedPrompt = PromptEntity.builder()
                .type("Expansion")
                .name("Updated Prompt")
                .content("Updated content")
                .build();
        when(promptRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> promptService.update(999L, updatedPrompt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Prompt not found: 999");
        verify(promptRepository).findById(999L);
        verify(promptRepository, never()).save(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // When
        promptService.delete(1L);

        // Then
        verify(promptRepository).deleteById(1L);
    }
}
