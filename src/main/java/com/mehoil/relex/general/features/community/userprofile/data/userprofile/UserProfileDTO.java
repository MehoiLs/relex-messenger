package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserProfileDTO {

    @JsonProperty("id")
    private final Long id;
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
    @JsonProperty("email")
    private final String email;
    @JsonProperty("role")
    private final String role;

}
