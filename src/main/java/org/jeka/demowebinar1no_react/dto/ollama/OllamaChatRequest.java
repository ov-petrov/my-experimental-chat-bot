package org.jeka.demowebinar1no_react.dto.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("messages")
    private List<OllamaMessage> messages;
    
    @JsonProperty("stream")
    private Boolean stream;
    
    @JsonProperty("options")
    private OllamaOptions options;
}
