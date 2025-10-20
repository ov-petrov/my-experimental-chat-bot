package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatEntryRepository extends JpaRepository<ChatEntryEntity, Long> {
    List<ChatEntryEntity> findByChatOrderByTimestampAsc(ChatEntity chat);
}


