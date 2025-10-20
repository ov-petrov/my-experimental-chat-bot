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
public class OllamaModelsResponse {
    
    @JsonProperty("models")
    private List<OllamaModel> models;
}
