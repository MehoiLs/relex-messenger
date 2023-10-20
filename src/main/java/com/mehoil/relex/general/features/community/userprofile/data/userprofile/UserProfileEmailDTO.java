package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class UserProfileEmailDTO {

    @Email(message = "Invalid E-Mail format")
    @JsonProperty("email")
    private final String email;

}
