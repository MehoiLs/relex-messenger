package root.general.main.data.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;
import root.general.main.data.User;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Value
@AllArgsConstructor
@Table(name = "email_change_tokens")
public class TokenChangeEmailDTO {

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

    public TokenChangeEmailDTO() {
        this.token = null;
        this.newEmail = null;
        this.user = null;
    }
}
