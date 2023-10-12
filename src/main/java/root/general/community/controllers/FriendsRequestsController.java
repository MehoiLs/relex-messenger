package root.general.community.controllers;

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
public class FriendsRequestsController {

    private final FriendRequestsService friendRequestsService;

    @Autowired
    public FriendsRequestsController(FriendRequestsService friendRequestsService) {
        this.friendRequestsService = friendRequestsService;
    }

    @GetMapping
    public ResponseEntity<?> getAllFriendsRequests(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(friendRequestsService.getAllFriendRequestsForUserAsString(user), HttpStatus.OK);
    }

    @PostMapping("/accept/user/{sender}")
    public ResponseEntity<?> acceptFriendRequest(@AuthenticationPrincipal User user,
                                                 @PathVariable String sender) {
        try {
            friendRequestsService.acceptFriendRequest(sender, user);
            return new ResponseEntity<>("Added " + sender + " as friend!", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/accept/all")
    public ResponseEntity<?> acceptAllFriendRequests(@AuthenticationPrincipal User user) {
        friendRequestsService.acceptAllFriendRequests(user);
        return new ResponseEntity<>("Successfully accepted all friend requests!", HttpStatus.OK);
    }

    @PostMapping("/deny/user/{sender}")
    public ResponseEntity<?> denyFriendRequest(@AuthenticationPrincipal User user,
                                               @PathVariable String sender) {
        try {
            friendRequestsService.denyFriendRequest(sender, user);
            return new ResponseEntity<>("Denied " + sender + "'s friend request.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/deny/all")
    public ResponseEntity<?> denyAllFriendRequests(@AuthenticationPrincipal User user) {
        friendRequestsService.denyAllFriendRequests(user);
        return new ResponseEntity<>("Successfully denied all friend requests.", HttpStatus.OK);
    }


}
