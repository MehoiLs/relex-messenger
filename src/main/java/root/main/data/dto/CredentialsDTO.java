package root.main.data.dto;

import lombok.Value;

@Value
public class CredentialsDTO {
    String login;
    String password;

    public CredentialsDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CredentialsDTO() {
        this.login = null;
        this.password = null;
    }
}
