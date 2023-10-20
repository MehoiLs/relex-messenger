package com.mehoil.relex.general.security.registration.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import com.mehoil.relex.general.user.data.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registration_tokens")
public class RegistrationToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private final String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private final User user;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

}
