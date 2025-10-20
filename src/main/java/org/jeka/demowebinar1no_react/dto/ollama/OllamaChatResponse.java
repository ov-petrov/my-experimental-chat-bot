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
public class OllamaChatResponse {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("message")
    private OllamaMessage message;
    
    @JsonProperty("done")
    private Boolean done;
    
    @JsonProperty("total_duration")
    private Long totalDuration;
    
    @JsonProperty("load_duration")
    private Long loadDuration;
    
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;
    
    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;
    
    @JsonProperty("eval_count")
    private Integer evalCount;
    
    @JsonProperty("eval_duration")
    private Long evalDuration;
}
