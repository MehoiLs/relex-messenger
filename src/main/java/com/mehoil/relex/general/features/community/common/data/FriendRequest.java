package com.mehoil.relex.general.features.community.common.data;

import jakarta.persistence.*;
import com.mehoil.relex.general.user.data.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "friend_requests")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    User recipient;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_at", nullable = false)
    LocalDateTime sentAt = LocalDateTime.now();

    public FriendRequest(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }
}

