package root.general.security.general.data.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CredentialsDTO {
    String login;
    String password;

    public CredentialsDTO() {
        this.login = null;
        this.password = null;
    }
}
