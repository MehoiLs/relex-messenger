package root.general.community.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import root.general.community.services.FriendRequestsService;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;

@RestController
@RequestMapping("/community/requests")
@Tag(
        name = "Операции запросов добавления в друзья в сообществе (community)",
        description = "Предоставляет API для просмотра заявок в друзья, одобрения или отклонения их.")
public class FriendsRequestsController {

    private final FriendRequestsService friendRequestsService;

    @Autowired
    public FriendsRequestsController(FriendRequestsService friendRequestsService) {
        this.friendRequestsService = friendRequestsService;
    }

    @Operation(
            summary = "Получить информацию о заявках в друзья",
            description = "Получить информацию о всех существующих заявках на добавление в друзья у конкретного пользователя.")
    @ApiResponse(
            responseCode = "200",
            description = "Информация о запросах в друзья получена успешно.",
            content = @Content(mediaType = "text/plain"))
    @GetMapping
    public ResponseEntity<String> getAllFriendsRequests(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(friendRequestsService.getAllFriendRequestsForUserAsString(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Принять заявку в друзья",
            description = "Принять заявку в друзья по имени другого пользователя (отправителя заявки).")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь был успешно добавлен в друзья. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "text/plain"))
    @PostMapping("/accept/user/{sender}")
    public ResponseEntity<String> acceptFriendRequest(@AuthenticationPrincipal User user,
                                                 @PathVariable String sender) {
        try {
            friendRequestsService.acceptFriendRequest(sender, user);
            return new ResponseEntity<>("Added " + sender + " as friend!", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Принять все заявки в друзья",
            description = "Принять все существующие заявки в друзья.")
    @ApiResponse(
            responseCode = "200",
            description = "Все пользователи (если заявки были) были успешно добавлены в друзья.",
            content = @Content(mediaType = "text/plain"))
    @PostMapping("/accept/all")
    public ResponseEntity<String> acceptAllFriendRequests(@AuthenticationPrincipal User user) {
        friendRequestsService.acceptAllFriendRequests(user);
        return new ResponseEntity<>("Successfully accepted all friend requests!", HttpStatus.OK);
    }

    @Operation(
            summary = "Отклонить заявку в друзья",
            description = "Отклонить заявку в друзья по имени другого пользователя (отправителя заявки).")
    @ApiResponse(
            responseCode = "200",
            description = "Заявка на добавления в друзья была успешно отклонена. " +
                    "Если пользователь не найден, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "text/plain"))
    @PostMapping("/deny/user/{sender}")
    public ResponseEntity<String> denyFriendRequest(@AuthenticationPrincipal User user,
                                               @PathVariable String sender) {
        try {
            friendRequestsService.denyFriendRequest(sender, user);
            return new ResponseEntity<>("Denied " + sender + "'s friend request.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(
            summary = "Отклонить все заявки в друзья",
            description = "Отклонить все существующие заявки в друзья.")
    @ApiResponse(
            responseCode = "200",
            description = "Все заявки (если таковые были) на добавления в друзья были успешно отклонены.",
            content = @Content(mediaType = "text/plain"))
    @PostMapping("/deny/all")
    public ResponseEntity<String> denyAllFriendRequests(@AuthenticationPrincipal User user) {
        friendRequestsService.denyAllFriendRequests(user);
        return new ResponseEntity<>("Successfully denied all friend requests.", HttpStatus.OK);
    }


}
