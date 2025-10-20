package org.jeka.demowebinar1no_react.dto.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaOptions {
    
    @JsonProperty("temperature")
    private Double temperature;
    
    @JsonProperty("top_p")
    private Double topP;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    @JsonProperty("repeat_penalty")
    private Double repeatPenalty;
}
