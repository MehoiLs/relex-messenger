package root.messaging.data;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {

    @Id
    private Long id;
    private String chatId;
    private Long senderId;
    private String senderName;
    private String content;

}
