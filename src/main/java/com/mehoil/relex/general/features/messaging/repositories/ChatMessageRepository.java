package com.mehoil.relex.general.features.messaging.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mehoil.relex.general.features.messaging.data.ChatMessage;
import com.mehoil.relex.general.features.messaging.data.enums.MessageStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    long countBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);
    long countByRecipientIdAndStatus(Long recipientId, MessageStatus status);
    List<ChatMessage> findByRecipientIdAndStatus(Long recipientId, MessageStatus status);
    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
    List<ChatMessage> findBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);
    List<ChatMessage> findByChatId(String chatId);

}
