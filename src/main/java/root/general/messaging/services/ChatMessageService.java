package root.general.messaging.services;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.general.main.utils.CryptoUtils;
import root.general.main.utils.MapperUtils;
import root.general.messaging.data.ChatMessage;
import root.general.messaging.data.ChatNotification;
import root.general.messaging.data.dto.ChatMessageDTO;
import root.general.messaging.data.enums.MessageStatus;
import root.general.messaging.exceptions.ChatServiceException;
import root.general.messaging.repositories.ChatMessageRepository;
import root.general.messaging.utils.MessagesUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
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

    public long countAllNewMessages(Long userId) {
        return chatMessageRepository.countByRecipientIdAndStatus(userId, MessageStatus.DELIVERED);
    }

    public long countNewMessagesFromUser (Long senderId, Long recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.DELIVERED);
    }

    public List<ChatMessageDTO> getAllNewMessages(Long userId) {
        List<ChatMessageDTO> dtoList = new ArrayList<>();
        List<ChatMessage> chatMessages =
                chatMessageRepository.findByRecipientIdAndStatus(userId, MessageStatus.DELIVERED);
        chatMessages.forEach(msg -> {
            updateMessageStatusToRead(msg);
            dtoList.add(MapperUtils.mapChatMessageToDto(msg));
        });
        return dtoList;
    }

    public List<ChatMessageDTO> getAllNewMessagesFromUser(Long senderId, Long recipientId) {
        List<ChatMessageDTO> dtoList = new ArrayList<>();
        List<ChatMessage> chatMessages = chatMessageRepository.findBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.DELIVERED);
        chatMessages.forEach(msg -> {
            updateMessageStatusToRead(msg);
            dtoList.add(MapperUtils.mapChatMessageToDto(msg));
        });
        return dtoList;
    }

    public ResponseEntity<byte[]> getChatMessagesHistoryToDownload(Long senderId, Long recipientId) throws ChatServiceException {
        String chatId = MessagesUtils.getChatIdBySenderAndRecipient(senderId, recipientId);
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(chatId);

        if(chatMessages.isEmpty()) throw new ChatServiceException("Cannot find any messages with user(id): " + recipientId);

        String chatHistory = MessagesUtils.buildStringFromMessagesList(chatMessages);
        byte[] chatHistoryBytes = chatHistory.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "chat_" + recipientId + ".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .body(chatHistoryBytes);
    }

    public void updateMessageStatusByNotification(ChatNotification notification) {
        updateMessageStatusToRead(get(notification.getId()));
    }

    private void updateMessageStatusToRead(@NonNull ChatMessage message) {
        message.setStatus(MessageStatus.READ);
        save(message);
    }

}
