package com.mehoil.relex.general.security.general.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invalid_jwt_tokens")
public class InvalidatedJwtToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    public InvalidatedJwtToken(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
