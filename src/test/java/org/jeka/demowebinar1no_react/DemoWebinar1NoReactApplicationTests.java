package org.jeka.demowebinar1no_react;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.ai.ollama.enabled=false",
        "spring.ai.vectorstore.pgvector.enabled=false"
})
class DemoWebinar1NoReactApplicationTests {

    @Test
    void contextLoads() {
    }

}
