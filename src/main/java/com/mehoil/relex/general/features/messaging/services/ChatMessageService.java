package com.mehoil.relex.general.features.messaging.services;

import com.mehoil.relex.general.features.messaging.data.ChatMessage;
import com.mehoil.relex.general.features.messaging.data.dto.ChatMessageDTO;
import com.mehoil.relex.general.features.messaging.data.dto.ChatMessageListDTO;
import com.mehoil.relex.general.features.messaging.data.enums.MessageStatus;
import com.mehoil.relex.general.features.messaging.exceptions.ChatServiceException;
import com.mehoil.relex.general.features.messaging.repositories.ChatMessageRepository;
import com.mehoil.relex.general.features.messaging.utils.MessagesUtils;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.mehoil.relex.shared.utils.CryptoUtils;
import com.mehoil.relex.shared.сomponents.UserMapper;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, MessageSource messageSource, UserMapper userMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
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

    public ChatMessageListDTO getAllNewMessages(Long userId) {
        List<ChatMessageDTO> dtoList = new ArrayList<>();
        List<ChatMessage> chatMessages =
                chatMessageRepository.findByRecipientIdAndStatus(userId, MessageStatus.DELIVERED);
        chatMessages.forEach(msg -> {
            updateMessageStatusToRead(msg);
            dtoList.add(userMapper.mapChatMessageToDto(msg));
        });
        return new ChatMessageListDTO(dtoList);
    }

    public ChatMessageListDTO getAllNewMessagesFromUserAsDto(Long senderId, Long recipientId) {
        List<ChatMessageDTO> dtoList = new ArrayList<>();
        List<ChatMessage> chatMessages = chatMessageRepository.
                findBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.DELIVERED);
        chatMessages.forEach(msg -> {
            updateMessageStatusToRead(msg);
            dtoList.add(userMapper.mapChatMessageToDto(msg));
        });
        return new ChatMessageListDTO(dtoList);
    }

    public ResponseEntity<InputStreamResource> getChatMessagesHistoryToDownload(Long senderId, Long recipientId) throws ChatServiceException {
        String chatId = MessagesUtils.getChatIdBySenderAndRecipient(senderId, recipientId);
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(chatId);

        if(chatMessages.isEmpty()) throw new ChatServiceException(
                messageSource.getMessage("error-chat-messages-not-found-by-id", new Object[]{recipientId}, Locale.getDefault())
        );

        String chatHistory = MessagesUtils.buildStringFromMessagesList(chatMessages);
        byte[] chatHistoryBytes = chatHistory.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(chatHistoryBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "chat_" + recipientId + ".txt");

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }

    public void updateMessageStatusToRead(@NonNull ChatMessage message) {
        message.setStatus(MessageStatus.READ);
        save(message);
    }

}
