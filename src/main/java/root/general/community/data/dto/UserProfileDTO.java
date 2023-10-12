package root.general.community.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfileDTO {

    Long id;
    String username;
    String personalStatus;
    String description;
    String firstName;
    String lastName;
    String email;
    String role;

    public UserProfileDTO() {
        this.id = null;
        this.username = null;
        this.personalStatus = null;
        this.description = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.role = null;
    }
}
