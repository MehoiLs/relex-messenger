package root.messaging.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.main.utils.AppUtils;
import root.main.utils.CryptoUtils;
import root.messaging.data.ChatMessage;
import root.messaging.data.ChatNotification;
import root.messaging.data.enums.MessageStatus;
import root.messaging.exceptions.ChatServiceException;
import root.messaging.repositories.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage get(Long id) {
        return chatMessageRepository.findById(id)
                .orElse(null);
    }

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public String getDecryptedContent(ChatMessage chatMessage) {
        return CryptoUtils.decryptPlainText(chatMessage.getContent());
    }

    public ChatMessage saveSentMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setStatus(MessageStatus.DELIVERED);
        chatMessage.setContent(
                CryptoUtils.encryptPlainText(chatMessage.getContent())
        );
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(Long senderId, Long recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.DELIVERED);
    }

    public String getAllNewMessagesAsString(Long senderId, Long recipientId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.DELIVERED);
        return AppUtils.buildStringFromMessagesList(chatMessages);
    }

    public String getChatMessagesHistoryAsString(Long senderId, Long recipientId) throws ChatServiceException {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messages;
        if(chatId.isPresent()) {
            messages = chatMessageRepository.findByChatId(chatId.get());
            return AppUtils.buildStringFromMessagesList(messages);
        }
        else throw new ChatServiceException("Cannot find any chat messages history with ID: " + recipientId + ".");
    }

    public void updateMessageStatusByNotification(ChatNotification notification) {
        ChatMessage msg = get(notification.getId());
        if(msg != null) {
            msg.setStatus(MessageStatus.READ);
            save(msg);
        }
    }

}
