package root.general.messaging.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.messaging.data.ChatMessage;
import root.general.messaging.data.enums.MessageStatus;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
    long countBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);
    long countByRecipientIdAndStatus(Long recipientId, MessageStatus status);
    List<ChatMessage> findByRecipientIdAndStatus(Long recipientId, MessageStatus status);
    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
    List<ChatMessage> findBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);
    List<ChatMessage> findByChatId(String chatId);

}
