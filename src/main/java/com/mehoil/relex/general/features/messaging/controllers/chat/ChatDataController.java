package com.mehoil.relex.general.features.messaging.controllers.chat;

import com.mehoil.relex.general.features.messaging.data.dto.ChatMessageListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.mehoil.relex.shared.dto.DefaultMessageDTO;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.features.messaging.data.dto.ChatMessageDTO;
import com.mehoil.relex.general.features.messaging.services.ChatMessageService;

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
    public ResponseEntity<ChatMessageListDTO> getAllUnreadMessages (@AuthenticationPrincipal User user) {
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
    public ResponseEntity<DefaultMessageDTO> countNewMessages(@PathVariable String username,
                                              @AuthenticationPrincipal User user) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(username);
        return new ResponseEntity<>(
                new DefaultMessageDTO(String.valueOf(
                        chatMessageService.countNewMessagesFromUser(senderUser.getId(), user.getId()))),
                HttpStatus.OK);
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
    public ResponseEntity<ChatMessageListDTO> getAllNewMessagesFromUser (@PathVariable String username,
                                                                         @AuthenticationPrincipal User user) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(username);
        return new ResponseEntity<>(
                chatMessageService.getAllNewMessagesFromUserAsDto(senderUser.getId(), user.getId()),
                HttpStatus.OK);
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
    public ResponseEntity<InputStreamResource> getChatMessageHistory (@PathVariable String username,
                                                                      @AuthenticationPrincipal User user) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(username);
        return chatMessageService.getChatMessagesHistoryToDownload(user.getId(), senderUser.getId());
    }

}
