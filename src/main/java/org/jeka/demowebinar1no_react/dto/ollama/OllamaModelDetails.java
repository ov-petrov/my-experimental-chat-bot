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
public class OllamaModelDetails {
    
    @JsonProperty("parent_model")
    private String parentModel;
    
    @JsonProperty("format")
    private String format;
    
    @JsonProperty("family")
    private String family;
    
    @JsonProperty("families")
    private String[] families;
    
    @JsonProperty("parameter_size")
    private String parameterSize;
    
    @JsonProperty("quantization_level")
    private String quantizationLevel;
}
