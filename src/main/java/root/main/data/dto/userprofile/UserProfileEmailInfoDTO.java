package root.main.data.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfileEmailInfoDTO {

    String email;

    public UserProfileEmailInfoDTO() {
        this.email = null;
    }
}
