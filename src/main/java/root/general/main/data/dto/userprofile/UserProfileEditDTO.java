package root.general.main.data.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfileEditDTO {

    @JsonProperty("username")
    String username;
    @JsonProperty("personal_status")
    String personalStatus;
    @JsonProperty("description")
    String description;
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;

    public UserProfileEditDTO() {
        this.username = null;
        this.personalStatus = null;
        this.description = null;
        this.firstName = null;
        this.lastName = null;
    }
}
