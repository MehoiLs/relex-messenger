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
import root.main.services.UserService;
import root.main.utils.MapperUtils;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
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
            userService.changeUserProfileInfo(user, editedProfile);
            UserProfileFullInfoDTO userProfile = MapperUtils.mapUserToUserFullProfileInfo(user);
            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
