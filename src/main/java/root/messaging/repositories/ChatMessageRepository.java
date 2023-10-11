package root.messaging.repositories;

import org.springframework.data.repository.CrudRepository;
import root.messaging.data.ChatMessage;
import root.messaging.data.enums.MessageStatus;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {
    long countBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, MessageStatus status);

    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatMessage> findByChatId(String chatId);

}
