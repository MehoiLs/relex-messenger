package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class UserProfilePasswordDTO {

    @JsonProperty("old_password")
    private final String oldPassword;
    @JsonProperty("new_password")
    private final String newPassword;

}
