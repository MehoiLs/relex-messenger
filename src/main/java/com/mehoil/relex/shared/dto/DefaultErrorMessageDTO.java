package com.mehoil.relex.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DefaultErrorMessageDTO {
    @JsonProperty("error_message")
    private final String errorMessage;
}
