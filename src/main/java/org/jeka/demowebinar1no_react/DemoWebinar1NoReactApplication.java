package org.jeka.demowebinar1no_react;

import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.service.DocumentLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class DemoWebinar1NoReactApplication {

    @Autowired
    private DocumentLoaderService documentLoaderService;

    public static void main(String[] args) {
        SpringApplication.run(DemoWebinar1NoReactApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartSteps() {
        log.info("Starting of documents loading");
        documentLoaderService.loadDocuments();
    }

}
