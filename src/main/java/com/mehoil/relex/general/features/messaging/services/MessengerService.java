package com.mehoil.relex.general.features.messaging.services;

import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.features.messaging.data.ChatMessage;
import com.mehoil.relex.general.features.messaging.data.ChatNotification;
import com.mehoil.relex.general.user.services.UserService;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessengerService {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public MessengerService(ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    public void sendPrivateMessageToUser(@NonNull ChatMessage chatMessage) {
        try {
            chatMessage.setSenderName(userService.getUserById(
                    chatMessage.getSenderId()).getUsername());
            chatMessage.setRecipientName(userService.getUserById(
                    chatMessage.getRecipientId()).getUsername());
        } catch (UserNotFoundException userNotFoundException) {
            return;
        }
        ChatMessage saved = chatMessageService.saveSentMessage(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),"/queue/messages/" + chatMessage.getChatId(),
                new ChatNotification(
                        saved.getId(),
                        saved.getChatId(),
                        saved.getSenderId(),
                        saved.getSenderName(),
                        chatMessageService.getDecryptedContent(saved)));
    }

    public void updateMessageStatusByNotification(@NonNull ChatNotification notification) {
        chatMessageService.updateMessageStatusToRead(chatMessageService.get(notification.getId()));
    }

}
