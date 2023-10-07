package root.main.data.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;
import root.main.data.User;

@Entity
@Value
@Table(name = "email_change_tokens")
@AllArgsConstructor
public class TokenChangeEmailDTO {

    @Id
    @Column(name = "token", nullable = false, unique = true)
    String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    public TokenChangeEmailDTO() {
        this.token = null;
        this.user = null;
    }
}
