package com.mehoil.relex.general.features.community.common.controllers;

import com.mehoil.relex.general.features.community.common.data.dto.FriendRequestDTO;
import com.mehoil.relex.general.features.community.common.data.dto.FriendRequestListDTO;
import com.mehoil.relex.general.features.community.common.exceptions.FriendRequestException;
import com.mehoil.relex.general.features.community.common.services.FriendRequestsService;
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

import java.util.List;

@RestController
@RequestMapping("/community/requests")
@Tag(
        name = "Операции запросов добавления в друзья в сообществе (community)",
        description = "Предоставляет API для просмотра заявок в друзья, одобрения или отклонения их.")
public class FriendsRequestsController {

    private final FriendRequestsService friendRequestsService;

    public FriendsRequestsController(FriendRequestsService friendRequestsService) {
        this.friendRequestsService = friendRequestsService;
    }

    @Operation(
            summary = "Получить информацию о заявках в друзья",
            description = "Получить информацию о всех существующих заявках на добавление в друзья у конкретного пользователя.")
    @ApiResponse(
            responseCode = "200",
            description = "Информация о запросах в друзья получена успешно.",
            content = @Content(mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<FriendRequestListDTO> getAllFriendsRequests(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                friendRequestsService.getAllFriendRequestsForUserAsDtoList (user),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Принять заявку в друзья",
            description = "Принять заявку в друзья по имени другого пользователя (отправителя заявки).")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь был успешно добавлен в друзья. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/accept/user/{sender}")
    public ResponseEntity<DefaultMessageDTO> acceptFriendRequest(@AuthenticationPrincipal User user,
                                                                 @PathVariable String sender) throws UserNotFoundException, FriendRequestException {
        String msg = friendRequestsService.acceptFriendRequest(sender, user);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

    @Operation(
            summary = "Принять все заявки в друзья",
            description = "Принять все существующие заявки в друзья.")
    @ApiResponse(
            responseCode = "200",
            description = "Все пользователи (если заявки были) были успешно добавлены в друзья.",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/accept/all")
    public ResponseEntity<DefaultMessageDTO> acceptAllFriendRequests(@AuthenticationPrincipal User user) {
        String msg = friendRequestsService.acceptAllFriendRequests(user);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонить заявку в друзья",
            description = "Отклонить заявку в друзья по имени другого пользователя (отправителя заявки).")
    @ApiResponse(
            responseCode = "200",
            description = "Заявка на добавления в друзья была успешно отклонена. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/deny/user/{sender}")
    public ResponseEntity<DefaultMessageDTO> denyFriendRequest(@AuthenticationPrincipal User user,
                                               @PathVariable String sender) throws UserNotFoundException {
        String msg = friendRequestsService.denyFriendRequest(sender, user);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонить все заявки в друзья",
            description = "Отклонить все существующие заявки в друзья.")
    @ApiResponse(
            responseCode = "200",
            description = "Все заявки (если таковые были) на добавления в друзья были успешно отклонены.",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/deny/all")
    public ResponseEntity<DefaultMessageDTO> denyAllFriendRequests(@AuthenticationPrincipal User user) {
        String msg = friendRequestsService.denyAllFriendRequests(user);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

}
