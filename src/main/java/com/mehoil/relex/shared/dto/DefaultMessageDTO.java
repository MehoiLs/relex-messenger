package com.mehoil.relex.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
@AllArgsConstructor
public class DefaultMessageDTO {
    @JsonProperty("message")
    String message;

    public DefaultMessageDTO() {
        this.message = null;
    }
}

