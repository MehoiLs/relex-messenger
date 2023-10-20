package com.mehoil.relex.general.features.messaging.controllers.chat;

import com.mehoil.relex.general.features.messaging.data.ChatMessage;
import com.mehoil.relex.general.features.messaging.services.MessengerService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.features.messaging.data.ChatNotification;
import com.mehoil.relex.general.features.messaging.services.ChatMessageService;

@Hidden
@Controller
public class ChatMessagesController {

    private final MessengerService messengerService;

    public ChatMessagesController(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        messengerService.sendPrivateMessageToUser(chatMessage);
    }

    @MessageMapping("/chat/read")
    public void processReadNotification(@Payload ChatNotification chatNotification) {
        messengerService.updateMessageStatusByNotification(chatNotification);
    }


}
