package root.general.community.controllers;

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
import root.general.main.services.user.UserService;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final UserCommunityService userCommunityService;
    private final FriendRequestsService friendRequestsService;

    @Autowired
    public CommunityController(UserCommunityService userCommunityService, FriendRequestsService friendRequestsService) {
        this.userCommunityService = userCommunityService;
        this.friendRequestsService = friendRequestsService;
    }

    @GetMapping
    public ResponseEntity<?> getGeneralInfo(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userCommunityService.getGeneralInfo(user), HttpStatus.OK);
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
            friendRequestsService.removeFriend(username, requesterUser);
            return new ResponseEntity<>("Successfully removed " + username + " from your friends list.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
