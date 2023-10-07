package root.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditDTO;
import root.main.data.dto.userprofile.UserProfileEmailDTO;
import root.main.data.dto.userprofile.UserProfileFullDTO;
import root.main.data.dto.userprofile.UserProfilePasswordDTO;
import root.main.exceptions.ProfilePictureUploadException;
import root.main.exceptions.UserProfileEditException;
import root.main.services.tokens.TokenChangeEmailService;
import root.main.services.community.UserProfileService;
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
    public ResponseEntity<UserProfileFullDTO> getUserProfileInfo(@AuthenticationPrincipal User user) {
        UserProfileFullDTO userProfile = MapperUtils.mapUserToUserFullProfileInfo(user);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @GetMapping("/edit")
    public ResponseEntity<UserProfileEditDTO> getUserProfileEditInfo(@AuthenticationPrincipal User user) {
        UserProfileEditDTO profileToEdit = MapperUtils.mapUserToUserEditProfileInfo(user);
        return new ResponseEntity<>(profileToEdit, HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUserProfileInfo(@AuthenticationPrincipal User user,
                                                 @RequestBody UserProfileEditDTO editedProfile) {
        try {
            UserProfileEditDTO changedProfile = MapperUtils.mapUserToUserEditProfileInfo(
                    userProfileService.changeUserProfileInfo(user, editedProfile)
            );
            return new ResponseEntity<>(changedProfile, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/edit/email")
    public ResponseEntity<String> requestEditEmail(@AuthenticationPrincipal User user,
                                              @RequestBody UserProfileEmailDTO emailInfoDTO) {
        try {
            String msg = userProfileService.requestChangeUserEmail(user, emailInfoDTO.getEmail());
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/edit/email")
    public ResponseEntity<UserProfileEmailDTO> requestEditEmail(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(new UserProfileEmailDTO(user.getEmail()), HttpStatus.OK);
    }

    @GetMapping("/edit/email/confirm/{token}")
    public ResponseEntity<?> getUserProfileEditInfo(@AuthenticationPrincipal User user,
                                                                         @PathVariable String token) {
        if (tokenChangeEmailService.confirmTokenForUser(token, user))
            return new ResponseEntity<>("You have successfully changed your e-mail!", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/edit/pfp")
    public ResponseEntity<String> uploadProfilePicture(@AuthenticationPrincipal User user,
                                                   @RequestParam("image") MultipartFile pfp) {
        try {
            String msg = userProfileService.uploadUserProfilePicture(user, pfp);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (ProfilePictureUploadException pictureUploadException) {
            return new ResponseEntity<>(pictureUploadException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping({"/pfp", "/edit/pfp"})
    public ResponseEntity<?> getProfilePicture(@AuthenticationPrincipal User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        InputStreamResource inputStream = userProfileService.getUserProfilePictureAsInputStreamResource(user);

        return new ResponseEntity<>(inputStream, headers, HttpStatus.OK);
    }

    @PostMapping("/edit/password")
    public ResponseEntity<?> editUserPassword(@AuthenticationPrincipal User user,
                                              @RequestBody UserProfilePasswordDTO userProfilePassword) {
        try {
            String msg = userProfileService.changeUserPassword(user, userProfilePassword);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user, HttpServletRequest request) {
        try {
            String msg = userProfileService.prepareUserForDelete(user, request);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.OK);
        }

    }



}
