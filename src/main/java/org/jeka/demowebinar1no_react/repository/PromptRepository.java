package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptRepository extends JpaRepository<PromptEntity, Long> {
    
    List<PromptEntity> findByType(String type);
    
    Optional<PromptEntity> findFirstByTypeOrderByCreatedAtDesc(String type);
    
    List<PromptEntity> findByNameContainingIgnoreCase(String name);
}


