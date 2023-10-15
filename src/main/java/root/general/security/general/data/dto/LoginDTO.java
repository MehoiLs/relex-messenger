package root.general.security.general.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class LoginDTO {

    @JsonProperty("message")
    String message;
    @JsonProperty("token")
    String token;

    public LoginDTO() {
        this.message = "";
        this.token = "";
    }
}
