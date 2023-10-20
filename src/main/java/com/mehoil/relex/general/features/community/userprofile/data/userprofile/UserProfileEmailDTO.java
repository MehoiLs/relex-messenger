package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileEmailDTO {

    @Email
    @JsonProperty("email")
    private String email;

}
