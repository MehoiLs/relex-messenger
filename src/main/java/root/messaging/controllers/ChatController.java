package root.messaging.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import root.main.data.User;
import root.main.exceptions.TokenIsInvalidatedException;
import root.main.exceptions.TokenNotFoundException;
import root.main.services.UserService;
import root.main.utils.AppUtils;
import root.messaging.data.ChatMessage;
import root.messaging.data.ChatNotification;
import root.messaging.services.ChatMessageService;
import root.messaging.services.ChatRoomService;
import root.security.general.components.JwtAuthenticationProvider;
import root.security.general.services.AuthenticationService;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final JwtAuthenticationProvider authenticationProvider;
    private final UserService userService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, ChatRoomService chatRoomService, JwtAuthenticationProvider authenticationProvider, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
    }

    @GetMapping("/login/chat")
    public String getLoginChatPage(Model model) {
        return "chat_login";
    }

    @GetMapping("/chat")
    public String getChatPage(HttpServletRequest request, Model model) {
        User user = authenticationProvider.getUserOrNullByToken(AppUtils.extractTokenFromRequest(request));
        if (user != null) {
            model.addAttribute("user", user);
            return "chat";
        }
        return "error";
    }

    @PostMapping("/login/chat")
    public String doLoginChat(@RequestParam String token, Model model) {
        try {
            Authentication auth = authenticationProvider.validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = userService.getUserByAuth(auth);
            model.addAttribute("user", user);
            return "redirect:/chat?token=" + token; //TODO fix auth
        } catch (TokenNotFoundException | TokenIsInvalidatedException e) {
            return "error";
        }
    }


    /****************************************************************/

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        //TODO CANT SEND TO YOURSELF
//        var chatId = chatRoomService
//                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
//        chatMessage.setChatId(chatId.get());
        //TODO TRY-CATCH
        chatMessage.setSenderName(userService.getUserById(
                chatMessage.getSenderId()).getUsername());
        chatMessage.setRecipientName(userService.getUserById(
                chatMessage.getRecipientId()).getUsername());

        ChatMessage saved = chatMessageService.save(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),"/queue/messages/" + chatMessage.getChatId(),
                new ChatNotification(
                        saved.getId(),
                        saved.getChatId(),
                        saved.getSenderId(),
                        saved.getSenderName(),
                        saved.getContent()));
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages ( @PathVariable Long senderId,
                                                @PathVariable Long recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable String id) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }
}

