package root.general.messaging.controllers.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import root.general.main.data.dto.DefaultMessageDTO;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.messaging.data.ChatMessage;
import root.general.messaging.data.dto.ChatMessageDTO;
import root.general.messaging.services.ChatMessageService;

import java.util.List;

@RestController
@Tag(
        name = "Информация о сообщениях чата",
        description = "Предоставляет информацию о непрочитанных сообщениях и истории чатов пользователей.")
public class ChatDataController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    public ChatDataController(ChatMessageService chatMessageService, UserService userService) {
        this.chatMessageService = chatMessageService;
        this.userService = userService;
    }

    @Operation(
            summary = "Получить информацию о всех непрочитанных сообщениях",
            description = "Получить информацию о всех непрочитанных сообщениях пользователя от его лица. "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о всех непрочитанных сообщениях получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/messages/unread")
    public ResponseEntity<List<ChatMessageDTO>> getAllUnreadMessages (@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                chatMessageService.getAllNewMessages(user.getId()),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о количестве непрочитанных сообщениях от конкретного пользователя",
            description = "Получить информацию о количестве непрочитанных сообщениях от конкретного " +
                    "пользователя от лица другого пользователя. "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о количестве непрочитанных сообщениях от конкретного пользователя получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/messages/user/{username}/unread/count")
    public ResponseEntity<?> countNewMessages(@PathVariable String username,
                                              @AuthenticationPrincipal User user) {
        try {
            User senderUser = userService.getUserByUsername(username);
            return new ResponseEntity<>(
                    new DefaultMessageDTO(String.valueOf(
                            chatMessageService.countNewMessagesFromUser(senderUser.getId(), user.getId()))),
                    HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Cannot find user: " + username),
                    HttpStatus.OK);
        }
    }

    @Operation(
            summary = "Получить информацию о всех непрочитанных сообщениях от конкретного пользователя",
            description = "Получить информацию о всех непрочитанных сообщениях от конкретного " +
                    "пользователя от лица другого пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о всех непрочитанных сообщениях от конкретного пользователя получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/messages/user/{username}/unread")
    public ResponseEntity<?> getAllNewMessagesFromUser (@PathVariable String username,
                                                        @AuthenticationPrincipal User user) {
        try {
            User senderUser = userService.getUserByUsername(username);
            return new ResponseEntity<>(
                    chatMessageService.getAllNewMessagesFromUser(senderUser.getId(), user.getId()),
                    HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Cannot find user: " + username),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получить все сообщения из чата с конкретным пользователем",
            description = "Получить все сообщения из чата с конкретным пользователем от лица другого пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о всех сообщениях от конкретного пользователя получена успешно.",
            content = @Content(mediaType = "application/octet-stream")
    )
    @GetMapping("/messages/user/{username}/all/download")
    public ResponseEntity<?> getChatMessageHistory (@PathVariable String username,
                                                    @AuthenticationPrincipal User user) {
        try {
            User senderUser = userService.getUserByUsername(username);
            return chatMessageService.getChatMessagesHistoryToDownload(user.getId(), senderUser.getId());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
