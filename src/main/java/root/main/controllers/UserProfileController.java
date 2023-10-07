package root.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import root.main.data.User;
import root.main.data.dto.UserProfileEditInfoDTO;
import root.main.data.dto.UserProfileFullInfoDTO;
import root.main.exceptions.UserProfileEditException;
import root.main.services.TokenChangeEmailService;
import root.main.services.UserProfileService;
import root.main.services.UserService;
import root.main.utils.MapperUtils;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final TokenChangeEmailService tokenChangeEmailService;

    @Autowired
    public UserProfileController(UserService userService, UserProfileService userProfileService, TokenChangeEmailService tokenChangeEmailService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    @GetMapping
    public ResponseEntity<UserProfileFullInfoDTO> getUserProfileInfo(@AuthenticationPrincipal User user) {
        UserProfileFullInfoDTO userProfile = MapperUtils.mapUserToUserFullProfileInfo(user);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @GetMapping("/edit")
    public ResponseEntity<UserProfileEditInfoDTO> getUserProfileEditInfo(@AuthenticationPrincipal User user) {
        UserProfileEditInfoDTO profileToEdit = MapperUtils.mapUserToUserEditProfileInfo(user);
        return new ResponseEntity<>(profileToEdit, HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUserProfileInfo(@AuthenticationPrincipal User user,
                                                 @RequestBody UserProfileEditInfoDTO editedProfile) {
        try {
            UserProfileFullInfoDTO changedProfile = MapperUtils.mapUserToUserFullProfileInfo(
                    userProfileService.changeUserProfileInfo(user, editedProfile)
            );
            return new ResponseEntity<>(changedProfile, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/edit/email")
    public ResponseEntity<String> requestEditEmail(@AuthenticationPrincipal User user,
                                              @RequestBody UserProfileEditInfoDTO editedProfile) {
        try {
            String msg = userProfileService.requestChangeUserEmail(user, editedProfile.getEmail());
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/edit/email/confirm/{token}")
    public ResponseEntity<?> getUserProfileEditInfo(@AuthenticationPrincipal User user,
                                                                         @PathVariable String token) {
        if (tokenChangeEmailService.confirmTokenForUser(token, user))
            return new ResponseEntity<>("You have successfully changed your e-mail!", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
