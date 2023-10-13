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
import root.general.community.exception.FriendRequestException;
import root.general.community.services.FriendRequestsService;
import root.general.community.services.UserCommunityService;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;

@RestController
@RequestMapping("/community")
@Tag(name = "Операции сообщества (community)", description = "Предоставляет API для просмотра информации " +
        "о других пользователях, а также для добавления и удаления пользователей из списка друзей ")
public class CommunityController {

    private final UserCommunityService userCommunityService;
    private final FriendRequestsService friendRequestsService;

    @Autowired
    public CommunityController(UserCommunityService userCommunityService, FriendRequestsService friendRequestsService) {
        this.userCommunityService = userCommunityService;
        this.friendRequestsService = friendRequestsService;
    }

    @Operation
            (summary = "Получить основную информацию о сообществе (community)",
                    description = "Получить информацию о текущем (аутентифицированном пользователе): новые сообщения, запросы в друзья")
    @ApiResponse
            (responseCode = "200", description = "Информация получена успешно.",
            content = @Content(mediaType = "text/plain"))
    @GetMapping
    public ResponseEntity<String> getGeneralInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userCommunityService.getGeneralInfoAsString(user), HttpStatus.OK);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserProfileInfo(@PathVariable String username) {
        try {
            return new ResponseEntity<>(userCommunityService.getUserProfileInfo(username), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{username}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable String username) {
        try {
            return new ResponseEntity<>(userCommunityService.getUserFriendsList(username), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users/{username}/add")
    public ResponseEntity<?> addUserAsFriend(@PathVariable String username,
                                             @AuthenticationPrincipal User requesterUser) {
        try {
            friendRequestsService.addFriend(username, requesterUser);
            return new ResponseEntity<>("Successfully sent the request to: " + username + ".", HttpStatus.OK);
        } catch (FriendRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getAllFriends(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userCommunityService.getAllFriends(user), HttpStatus.OK);
    }

    @PostMapping("/friends/{username}/remove")
    public ResponseEntity<?> removeUserAsFriend(@PathVariable String username,
                                                @AuthenticationPrincipal User requesterUser) {
        try {
            userCommunityService.removeFriend(username, requesterUser);
            return new ResponseEntity<>("Successfully removed " + username + " from your friends list.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
