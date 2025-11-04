package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jeka.demowebinar1no_react.model.LoadedDocumentEntity;
import org.jeka.demowebinar1no_react.repository.LoadedDocumentRepository;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentLoaderService {

    private final LoadedDocumentRepository loadedDocumentRepository;
    private final ResourcePatternResolver resourcePatternResolver;
    private final VectorStore vectorStore;

    @SneakyThrows
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void loadDocuments() {
        List<Resource> resourceList = Arrays.stream(
                resourcePatternResolver.getResources("classpath:/knowledgebase/**/*.txt")).toList();
        resourceList.stream()
                .map(r -> Pair.of(r, getContentHash(r)))
                .filter(r -> !loadedDocumentRepository.existsByFilenameAndContentHash(
                        r.getFirst().getFilename(),
                        r.getSecond())
                )
                .forEach(resourcePair -> {
                    var documents = new TextReader(resourcePair.getFirst()).get();
                    var textSplitter = TokenTextSplitter.builder().withChunkSize(500).build();
                    var chunks = textSplitter.apply(documents);
                    vectorStore.accept(chunks);

                    LoadedDocumentEntity loadedDocument = LoadedDocumentEntity.builder()
                            .documentType("txt")
                            .chunkCount(chunks.size())
                            .filename(resourcePair.getFirst().getFilename())
                            .contentHash(resourcePair.getSecond())
                            .build();
                    loadedDocumentRepository.save(loadedDocument);
                });
    }

    @SneakyThrows
    private String getContentHash(Resource r) {
        return DigestUtils.md5DigestAsHex(r.getContentAsByteArray());
    }
}
