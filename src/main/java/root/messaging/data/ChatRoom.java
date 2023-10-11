package root.messaging.data;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id", nullable = false, unique = true)
    private String chatId;
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

}
