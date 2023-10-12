package root.general.main.data.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfileEmailDTO {
    String email;

    public UserProfileEmailDTO() {
        this.email = null;
    }
}
