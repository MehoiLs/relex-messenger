package com.mehoil.relex.general.security.general.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "invalid_jwt_tokens")
public class InvalidatedJwtToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private final String token;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final Date expirationDate;

}
