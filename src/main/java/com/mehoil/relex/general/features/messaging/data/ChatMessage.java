package com.mehoil.relex.general.features.messaging.data;

import com.mehoil.relex.general.features.messaging.data.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "chat_id", nullable = false)
    private String chatId;
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;
    @Column(name = "sender_name", nullable = false)
    private String senderName;
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    @Column(name = "status", nullable = false)
    private MessageStatus status;

}
