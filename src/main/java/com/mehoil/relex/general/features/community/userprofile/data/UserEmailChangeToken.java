package com.mehoil.relex.general.features.community.userprofile.data;

import com.mehoil.relex.general.user.data.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_change_tokens")
public class UserEmailChangeToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", nullable = false, unique = true)
    private final String token;

    @Column(name = "new_email", nullable = false, unique = true)
    private final String newEmail;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private final User user;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1);

}
