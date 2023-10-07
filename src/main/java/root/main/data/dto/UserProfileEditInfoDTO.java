package root.main.data.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class UserProfileEditInfoDTO {

    String username;
    String firstName;
    String lastName;
    String email;

    public UserProfileEditInfoDTO() {
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
    }

}
