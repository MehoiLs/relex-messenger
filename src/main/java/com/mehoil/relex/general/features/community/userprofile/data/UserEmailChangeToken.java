package com.mehoil.relex.general.features.community.userprofile.data;

import com.mehoil.relex.general.user.data.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_change_tokens")
public class UserEmailChangeToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "new_email", nullable = false, unique = true)
    private String newEmail;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1);

    public UserEmailChangeToken(String token, String newEmail, User user) {
        this.token = token;
        this.newEmail = newEmail;
        this.user = user;
    }
}
