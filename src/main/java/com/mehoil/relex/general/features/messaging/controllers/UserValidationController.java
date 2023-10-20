package com.mehoil.relex.general.features.messaging.controllers;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@Tag(
        name = "Валидация пользователей чата",
        description = "Производит валидацию пользователей на основе их юзернеймов, ID и списке друзей.")
public class UserValidationController {

    private final UserService userService;

    public UserValidationController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Проверка доступности пользователя",
            description = "Получить информацию о доступности пользователя на основе его существования и списка его друзей. "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о доступности пользователя успешно получена. " +
                    "\"false\" - если пользователя не существует, или получение сообщений ограничено списком друзей" +
                    "пользователя, в котором запрашивающая сторона не находится; " +
                    "\"true\" - если пользователь существует и ему можно отправить сообщение.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/public/validation/users/accessible/{userIdToCheck}/to/{issuerId}")
    public ResponseEntity<Map<String, Boolean>> checkUserIsAccessible(
            @PathVariable Long userIdToCheck,
            @PathVariable Long issuerId) throws UserNotFoundException {

        boolean userIsAccessible = false;

        if (userService.getUserById(userIdToCheck).isAccessibilityFriendsOnly())
            userIsAccessible = userService.userIsFriendsWith(userIdToCheck, issuerId);

        Map<String, Boolean> response = Collections.singletonMap("accessible", userIsAccessible);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение ID пользователя по юзернейму",
            description = "Получение ID пользователя по предоставленному юзернейму. "
    )
    @ApiResponse(
            responseCode = "200",
            description = "Вернёт ID пользователя по юзернейму, если таковой существует. " +
                    "Если же пользователя не существует, будет возвращен код состояния `NOT_FOUND` ",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/public/validation/users/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) throws UserNotFoundException {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user.getId());
    }



}
