package root.general.main.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Entity
@Value
@AllArgsConstructor
@Table(name = "email_change_tokens")
public class TokenChangeEmail {

    @Id
    @Column(name = "token", nullable = false, unique = true)
    String token;

    @Column(name = "new_email", nullable = false, unique = true)
    String newEmail;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1);

    public TokenChangeEmail() {
        this.token = null;
        this.newEmail = null;
        this.user = null;
    }
}
