package root.messaging.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import root.main.services.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*********************************** AJAX ***********************************/
    @GetMapping("/api/users/accessible/{userIdToCheck}/to/{issuerId}")
    public ResponseEntity<Map<String, Boolean>> checkUserIsAccessible(
            @PathVariable Long userIdToCheck,
            @PathVariable Long issuerId) {

        boolean userIsAccessible = false;
        if(userService.userExistsById(userIdToCheck)) {
            userIsAccessible = true;
            if (userService.getUserById(userIdToCheck).isAccessibilityIsFriendsOnly())
                userIsAccessible = userService.userIsFriendsWith(userIdToCheck, issuerId);
        }

        Map<String, Boolean> response = Collections.singletonMap("accessible", userIsAccessible);
        return ResponseEntity.ok(response);
    }



}
