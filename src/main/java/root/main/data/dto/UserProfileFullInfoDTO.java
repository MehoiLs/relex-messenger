package root.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class UserProfileFullInfoDTO {

    Long id;
    String username;
    String firstName;
    String lastName;
    String email;
    String role;
    List<String> friendsList;
    boolean locked;

    public UserProfileFullInfoDTO() {
        this.id = null;
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.role = null;
        this.friendsList = null;
        this.locked = true;
    }

}
