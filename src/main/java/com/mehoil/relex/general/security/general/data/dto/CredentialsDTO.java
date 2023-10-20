package com.mehoil.relex.general.security.general.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class CredentialsDTO {

    @JsonProperty("login")
    private final String login;
    @JsonProperty("password")
    private final String password;
}
