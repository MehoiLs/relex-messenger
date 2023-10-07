package root.security.general.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;

@Value
@Entity
@Table(name = "invalid_jwt_tokens")
@AllArgsConstructor
public class InvalidatedJwtToken {

    @Id
    @Column(name = "token")
    String token;

    public InvalidatedJwtToken() {
        this.token = null;
    }
}
