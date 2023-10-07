package root.main.data.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class UserProfileFullDTO {

    Long id;
    String username;
    String personalStatus;
    String description;
    String firstName;
    String lastName;
    String email;
    String role;
    List<String> friendsList;
    boolean locked;

    public UserProfileFullDTO() {
        this.id = null;
        this.username = null;
        this.personalStatus = null;
        this.description = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.role = null;
        this.friendsList = null;
        this.locked = true;
    }

}
