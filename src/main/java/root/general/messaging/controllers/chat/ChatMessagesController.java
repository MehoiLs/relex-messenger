package root.general.messaging.controllers.chat;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.messaging.data.ChatMessage;
import root.general.messaging.data.ChatNotification;
import root.general.messaging.services.ChatMessageService;

@Hidden
@Controller
public class ChatMessagesController {

    private final UserService userService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessagesController(UserService userService, ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
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

    @MessageMapping("/chat/read")
    public void processReadNotification(@Payload ChatNotification chatNotification) {
        chatMessageService.updateMessageStatusByNotification(chatNotification);
    }


}
