package root.main.data.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;
import root.main.data.User;

import java.util.Date;

@Entity
@Value
@Table(name = "email_change_tokens")
@AllArgsConstructor
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
    Date expirationDate = new Date(new Date().getTime() + (1000*60*60*24));

    public TokenChangeEmailDTO() {
        this.token = null;
        this.newEmail = null;
        this.user = null;
    }
}
