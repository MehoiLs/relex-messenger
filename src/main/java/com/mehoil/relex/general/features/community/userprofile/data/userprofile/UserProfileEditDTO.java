package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class UserProfileEditDTO {

    @JsonProperty("username")
    private final String username;
    @JsonProperty("personal_status")
    private final String personalStatus;
    @JsonProperty("description")
    private final String description;
    @JsonProperty("first_name")
    private final String firstName;
    @JsonProperty("last_name")
    private final String lastName;

}
