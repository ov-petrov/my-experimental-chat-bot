package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.LoadedDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadedDocumentRepository extends JpaRepository<LoadedDocumentEntity, Long> {

    boolean existsByFilenameAndContentHash(String filename, String contentHash);
}
