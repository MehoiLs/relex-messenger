package root.general.security.general.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@Entity
@Table(name = "invalid_jwt_tokens")
public class InvalidatedJwtToken {

    @Id
    @Column(name = "token")
    String token;

    public InvalidatedJwtToken() {
        this.token = null;
    }
}
