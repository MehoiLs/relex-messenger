package com.mehoil.relex.general.features.community.common.controllers;

import com.mehoil.relex.general.features.community.common.data.dto.UserFriendListDTO;
import com.mehoil.relex.general.features.community.common.exceptions.CommunityException;
import com.mehoil.relex.general.features.community.common.exceptions.FriendRequestException;
import com.mehoil.relex.general.features.community.common.exceptions.UserPrivacyException;
import com.mehoil.relex.general.features.community.common.services.FriendRequestsService;
import com.mehoil.relex.general.features.community.common.services.CommunityService;
import com.mehoil.relex.general.features.community.common.data.dto.CommunityInfoDTO;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileDTO;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.shared.dto.DefaultMessageDTO;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community")
@Tag(
        name = "Операции сообщества (community)",
        description = "Предоставляет API для просмотра информации " +
        "о других пользователях, а также для добавления и удаления пользователей из списка друзей ")
public class CommunityController {

    private final CommunityService communityService;
    private final FriendRequestsService friendRequestsService;

    public CommunityController(CommunityService communityService, FriendRequestsService friendRequestsService) {
        this.communityService = communityService;
        this.friendRequestsService = friendRequestsService;
    }

    @Operation(
            summary = "Получить основную информацию о сообществе (community)",
            description = "Получить информацию о текущем (аутентифицированном пользователе): новые сообщения, запросы в друзья")
    @ApiResponse(
            responseCode = "200",
            description = "Информация о сообществе получена успешно.",
            content = @Content(mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<CommunityInfoDTO> getGeneralInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                communityService.getGeneralInfoAsDto(user),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о профиле пользователя",
            description = "Получить информацию о профиле пользователя по его имени пользователя, от лица другого пользователя. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о профиле конкретного пользователя получена успешно.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfileInfo(@PathVariable String username) throws UserNotFoundException {
        return new ResponseEntity<>(
                communityService.getUserProfileInfoAsDto(username),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о друзьях пользователя",
            description = "Получить информацию о друзьях пользователя по его имени пользователя, от лица другого пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Информация о друзьях пользователя получена успешно. (Если друзья скрыты, лист - пустой). " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/users/{username}/friends")
    public ResponseEntity<UserFriendListDTO> getUserFriends(@PathVariable String username)
            throws UserNotFoundException, UserPrivacyException {
        return new ResponseEntity<>(
                communityService.getUserFriendsByUsernameAsDto(username),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Добавить пользователя в друзья",
            description = "Добавить пользователя в друзья по его имени пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Запрос на добавление в друзья успешно отправлен. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/users/{username}/add")
    public ResponseEntity<DefaultMessageDTO> addUserAsFriend(@PathVariable String username,
                                                             @AuthenticationPrincipal User requesterUser)
            throws UserNotFoundException, FriendRequestException {
        String msg = friendRequestsService.sendFriendRequest(username, requesterUser);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о своих друзьях",
            description = "Добавить информацию о своих друзьях"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список друзей был успешно получен.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/friends")
    public ResponseEntity<UserFriendListDTO> getPersonalFriends(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                communityService.getUserFriendsAsDto(user),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить пользователя из друзей",
            description = "Удалить пользователя из друзей по его имени пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь был успешно удалён из друзей. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/friends/{username}/remove")
    public ResponseEntity<DefaultMessageDTO> removeUserAsFriend(@PathVariable String username,
                                                @AuthenticationPrincipal User requesterUser) throws UserNotFoundException, CommunityException {
        String msg = communityService.removeFriend(username, requesterUser);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

}
