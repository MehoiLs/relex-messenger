package root.general.community.data;

import jakarta.persistence.*;
import lombok.*;
import root.general.main.data.User;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@Entity
@Table(name = "friend_requests")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
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

    public FriendRequest() {
        this.id = null;
        this.sender = null;
        this.recipient = null;
    }
}
