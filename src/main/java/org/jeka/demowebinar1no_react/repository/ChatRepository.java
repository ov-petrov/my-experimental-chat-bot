package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
}


