package root.general.main.data.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfilePasswordDTO {

    @JsonProperty("old_password")
    String oldPassword;
    @JsonProperty("new_password")
    String newPassword;

    public UserProfilePasswordDTO() {
        this.oldPassword = null;
        this.newPassword = null;
    }
}
