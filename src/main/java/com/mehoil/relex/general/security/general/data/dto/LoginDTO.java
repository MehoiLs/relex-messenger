package com.mehoil.relex.general.security.general.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class LoginDTO {

    @JsonProperty("message")
    private final String message;
    @JsonProperty("token")
    private final String token;

}
