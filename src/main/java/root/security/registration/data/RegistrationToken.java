package root.security.registration.data;

import jakarta.persistence.*;
import lombok.Value;
import root.main.data.User;

import java.util.Date;
import java.util.UUID;

@Entity
@Value
@Table(name = "registration_tokens")
public class RegistrationToken {

    @Id
    @Column(name = "token", nullable = false, unique = true)
    String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date expirationDate = new Date(new Date().getTime() + (1000*60*60*24));

    public RegistrationToken(String token, User user) {;
        this.token = token;
        this.user = user;
    }

    public RegistrationToken() {
        this.token = null;
        this.user = null;
    }
}
