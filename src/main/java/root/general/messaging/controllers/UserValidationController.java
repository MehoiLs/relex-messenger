package root.general.messaging.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import root.general.main.data.User;
import root.general.main.services.user.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserValidationController {

    private final UserService userService;

    @Autowired
    public UserValidationController(UserService userService) {
        this.userService = userService;
    }

    /*********************************** AJAX ***********************************/
    @GetMapping("/public/validation/users/accessible/{userIdToCheck}/to/{issuerId}")
    public ResponseEntity<Map<String, Boolean>> checkUserIsAccessible(
            @PathVariable Long userIdToCheck,
            @PathVariable Long issuerId) {

        boolean userIsAccessible = false;
        if(userService.userExistsById(userIdToCheck)) {
            userIsAccessible = true;
            if (userService.getUserById(userIdToCheck).isAccessibilityFriendsOnly())
                userIsAccessible = userService.userIsFriendsWith(userIdToCheck, issuerId);
        }

        Map<String, Boolean> response = Collections.singletonMap("accessible", userIsAccessible);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/validation/users/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user.getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
