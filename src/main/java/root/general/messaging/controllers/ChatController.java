package root.general.messaging.controllers;

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
import root.general.main.data.User;
import root.general.main.services.user.UserService;
import root.general.main.utils.AppUtils;
import root.general.messaging.data.ChatMessage;
import root.general.messaging.data.ChatNotification;
import root.general.messaging.services.ChatMessageService;
import root.general.security.general.components.JwtAuthenticationProvider;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final JwtAuthenticationProvider authenticationProvider;
    private final UserService userService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageService chatMessageService,
                          JwtAuthenticationProvider authenticationProvider,
                          UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
    }

    @GetMapping("/login/chat")
    public String getLoginChatPage() {
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

            response.addCookie(
                    authenticationProvider.createCookieByToken(token)
            );

            return "redirect:/chat";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/error";
        }
    }


    /****************************** MESSAGE MAPPINGS ******************************/

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
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

    @GetMapping("/messages/unread")
    public ResponseEntity<?> getAllUnreadMessages (@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(chatMessageService.getAllNewMessagesAsString(user.getId()), HttpStatus.OK);
    }

    @GetMapping("/messages/{recipient}/count")
    public ResponseEntity<?> countNewMessages(@PathVariable String recipient,
                                              @AuthenticationPrincipal User user) {
        User recipientUser = userService.getUserByUsername(recipient);
        if(recipientUser != null)
            return new ResponseEntity<>(chatMessageService.countNewMessagesFromUser(user.getId(), recipientUser.getId()),
                    HttpStatus.OK);
        else
            return new ResponseEntity<>("Cannot find user: " + recipient, HttpStatus.OK);
    }

    @GetMapping("/messages/{recipient}/unread")
    public ResponseEntity<String> getAllNewMessagesFromUser (@PathVariable String recipient,
                                                             @AuthenticationPrincipal User user) {
        User recipientUser = userService.getUserByUsername(recipient);
        if(recipientUser != null)
            return new ResponseEntity<>(chatMessageService.getAllNewMessagesFromUserAsString(user.getId(), recipientUser.getId()),
                    HttpStatus.OK);
        else
            return new ResponseEntity<>("Cannot find user: " + recipient, HttpStatus.OK);
    }

    @GetMapping("/messages/{recipient}/all")
    public ResponseEntity<?> getChatMessageHistory (@PathVariable String recipient,
                                                    @AuthenticationPrincipal User user) {
        User recipientUser = userService.getUserByUsername(recipient);
        try {
            return chatMessageService.getChatMessagesHistoryToDownload(user.getId(), recipientUser.getId());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}

