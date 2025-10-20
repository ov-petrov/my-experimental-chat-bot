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
public class OllamaModel {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("modified_at")
    private String modifiedAt;
    
    @JsonProperty("size")
    private Long size;
    
    @JsonProperty("digest")
    private String digest;
    
    @JsonProperty("details")
    private OllamaModelDetails details;
}
