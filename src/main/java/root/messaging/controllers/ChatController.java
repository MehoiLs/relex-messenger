package root.messaging.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import root.messaging.exceptions.ChatServiceException;
import root.messaging.services.ChatMessageService;
import root.messaging.services.ChatRoomService;
import root.security.general.components.JwtAuthenticationProvider;

import java.util.Collections;
import java.util.Map;

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
        String token = AppUtils.extractTokenFromCookie(request);
        User user = authenticationProvider.getUserOrNullByToken(token);
        if (user != null) {
            model.addAttribute("user", user);
            return "chat";
        } else {
            model.addAttribute("error", "Could not authorize.");
            return "error";
        }
    }

    @PostMapping("/login/chat")
    public String doLoginChat(@RequestParam String token, HttpServletResponse response, Model model) {
        try {
            Authentication auth = authenticationProvider.validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = userService.getUserByAuth(auth);
            model.addAttribute("user", user);

            Cookie cookie = new Cookie("accessToken", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");

            response.addCookie(cookie);

            return "redirect:/chat";
        } catch (TokenNotFoundException | TokenIsInvalidatedException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    /****************************** MESSAGE MAPPINGS ******************************/

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        //TODO CANT SEND TO YOURSELF
        chatMessage.setSenderName(userService.getUserById(
                chatMessage.getSenderId()).getUsername());
        chatMessage.setRecipientName(userService.getUserById(
                chatMessage.getRecipientId()).getUsername());

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

    /****************************** MESSAGE MAPPINGS ******************************/

    @GetMapping("/messages/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable Long recipientId,
                                                 @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(chatMessageService.countNewMessages(user.getId(), recipientId), HttpStatus.OK);
    }

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<String> getAllNewMessages (@PathVariable Long recipientId,
                                                 @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(chatMessageService.getAllNewMessagesAsString(user.getId(), recipientId), HttpStatus.OK);
    }

    @GetMapping("/messages/{recipientId}/all")
    public ResponseEntity<?> getChatMessageHistory (@PathVariable Long recipientId,
                                                    @AuthenticationPrincipal User user) {
        try {
            String messages = chatMessageService.getChatMessagesHistoryAsString(user.getId(), recipientId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (ChatServiceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

