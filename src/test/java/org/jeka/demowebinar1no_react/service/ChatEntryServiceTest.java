package org.jeka.demowebinar1no_react.service;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.Role;
import org.jeka.demowebinar1no_react.repository.ChatEntryRepository;
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
class ChatEntryServiceTest {

    @Mock
    private ChatEntryRepository chatEntryRepository;

    @InjectMocks
    private ChatEntryService chatEntryService;

    private ChatEntity testChat;
    private ChatEntryEntity testEntry;

    @BeforeEach
    void setUp() {
        testChat = ChatEntity.builder()
                .id(1L)
                .title("Test Chat")
                .createdAt(LocalDateTime.now())
                .build();

        testEntry = ChatEntryEntity.builder()
                .id(1L)
                .chat(testChat)
                .role(Role.USER)
                .content("Hello, world!")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void findByChat_ShouldReturnEntriesForChat() {
        // Given
        List<ChatEntryEntity> expectedEntries = Arrays.asList(testEntry);
        when(chatEntryRepository.findByChatOrderByTimestampAsc(testChat)).thenReturn(expectedEntries);

        // When
        List<ChatEntryEntity> result = chatEntryService.findByChat(testChat);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello, world!");
        verify(chatEntryRepository).findByChatOrderByTimestampAsc(testChat);
    }

    @Test
    void findById_WhenEntryExists_ShouldReturnEntry() {
        // Given
        when(chatEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // When
        Optional<ChatEntryEntity> result = chatEntryService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("Hello, world!");
        verify(chatEntryRepository).findById(1L);
    }

    @Test
    void findById_WhenEntryNotExists_ShouldReturnEmpty() {
        // Given
        when(chatEntryRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<ChatEntryEntity> result = chatEntryService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(chatEntryRepository).findById(999L);
    }

    @Test
    void create_ShouldSaveAndReturnEntry() {
        // Given
        ChatEntryEntity newEntry = ChatEntryEntity.builder()
                .chat(testChat)
                .role(Role.USER)
                .content("New message")
                .build();
        when(chatEntryRepository.save(any(ChatEntryEntity.class))).thenReturn(testEntry);

        // When
        ChatEntryEntity result = chatEntryService.create(newEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello, world!");
        verify(chatEntryRepository).save(any(ChatEntryEntity.class));
    }

    @Test
    void update_WhenEntryExists_ShouldUpdateAndReturnEntry() {
        // Given
        ChatEntryEntity updatedEntry = ChatEntryEntity.builder()
                .role(Role.ASSISTANT)
                .content("Updated message")
                .build();
        when(chatEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(chatEntryRepository.save(any(ChatEntryEntity.class))).thenReturn(updatedEntry);

        // When
        ChatEntryEntity result = chatEntryService.update(1L, updatedEntry);

        // Then
        assertThat(result.getContent()).isEqualTo("Updated message");
        verify(chatEntryRepository).findById(1L);
        verify(chatEntryRepository).save(any(ChatEntryEntity.class));
    }

    @Test
    void update_WhenEntryNotExists_ShouldThrowException() {
        // Given
        ChatEntryEntity updatedEntry = ChatEntryEntity.builder()
                .role(Role.ASSISTANT)
                .content("Updated message")
                .build();
        when(chatEntryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatEntryService.update(999L, updatedEntry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Chat entry not found: 999");
        verify(chatEntryRepository).findById(999L);
        verify(chatEntryRepository, never()).save(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // When
        chatEntryService.delete(1L);

        // Then
        verify(chatEntryRepository).deleteById(1L);
    }
}
