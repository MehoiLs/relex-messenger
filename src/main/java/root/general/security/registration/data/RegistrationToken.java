package root.general.security.registration.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;
import root.general.main.data.User;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
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
    LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

    public RegistrationToken() {
        this.token = null;
        this.user = null;
    }
}
