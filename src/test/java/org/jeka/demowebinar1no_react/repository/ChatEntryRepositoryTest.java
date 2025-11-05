package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatEntryRepository chatEntryRepository;

    @Autowired
    private ChatRepository chatRepository;

    private ChatEntity testChat;
    private ChatEntryEntity testEntry;

    @BeforeEach
    void setUp() {
        testChat = ChatEntity.builder()
                .title("Test Chat")
                .build();
        testEntry = ChatEntryEntity.builder()
                .chat(testChat)
                .role(Role.USER)
                .content("Hello, world!")
                .build();
        testChat.addEntry(testEntry);

        testChat = entityManager.persistAndFlush(testChat);
    }

    @Test
    void save_ShouldPersistEntry() {
        // When
        ChatEntryEntity saved = chatEntryRepository.save(testEntry);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("Hello, world!");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.getChat().getId()).isEqualTo(testChat.getId());
    }

    @Test
    void findById_WhenEntryExists_ShouldReturnEntry() {
        // Given
        ChatEntryEntity saved = entityManager.persistAndFlush(testEntry);

        // When
        Optional<ChatEntryEntity> found = chatEntryRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Hello, world!");
        assertThat(found.get().getChat().getId()).isEqualTo(testChat.getId());
    }

    @Test
    void findByChatOrderByTimestampAsc_ShouldReturnEntriesInOrder() {
        // Given
        ChatEntity anotherChat = ChatEntity.builder()
                .title("Another Chat")
                .createdAt(LocalDateTime.now())
                .build();
        anotherChat = entityManager.persistAndFlush(anotherChat);

        ChatEntryEntity entry1 = ChatEntryEntity.builder()
                .chat(testChat)
                .role(Role.USER)
                .content("First message")
                .timestamp(LocalDateTime.now().minusHours(2))
                .build();
        ChatEntryEntity entry2 = ChatEntryEntity.builder()
                .chat(testChat)
                .role(Role.ASSISTANT)
                .content("Second message")
                .timestamp(LocalDateTime.now().minusHours(1))
                .build();
        ChatEntryEntity entry3 = ChatEntryEntity.builder()
                .chat(anotherChat)
                .role(Role.USER)
                .content("Different chat message")
                .timestamp(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        // When
        List<ChatEntryEntity> entries = chatEntryRepository.findByChatOrderByTimestampAsc(testChat);

        // Then
        assertThat(entries).hasSize(3);
        assertThat(entries).extracting(ChatEntryEntity::getContent)
                .containsExactly("Hello, world!", "First message", "Second message");
    }

    @Test
    @Disabled
    void delete_ShouldRemoveEntry() {
        // When
        chatEntryRepository.deleteById(testEntry.getId());
        entityManager.flush();

        // Then
        Optional<ChatEntryEntity> found = chatEntryRepository.findById(testEntry.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteChat_ShouldCascadeDeleteEntries() {
        // Given
        ChatEntryEntity saved = entityManager.persistAndFlush(testEntry);
        Long chatId = testChat.getId();
        Long entryId = saved.getId();

        // When
        chatRepository.deleteById(chatId);
        entityManager.flush();

        // Then
        Optional<ChatEntity> foundChat = chatRepository.findById(chatId);
        Optional<ChatEntryEntity> foundEntry = chatEntryRepository.findById(entryId);
        assertThat(foundChat).isEmpty();
        assertThat(foundEntry).isEmpty();
    }
}
