package root.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditInfoDTO;
import root.main.data.dto.userprofile.UserProfileEmailInfoDTO;
import root.main.data.dto.userprofile.UserProfileFullInfoDTO;
import root.main.exceptions.ProfilePictureUploadException;
import root.main.exceptions.UserProfileEditException;
import root.main.services.TokenChangeEmailService;
import root.main.services.UserProfileService;
import root.main.services.UserService;
import root.main.utils.MapperUtils;

import java.io.ByteArrayInputStream;

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
            UserProfileEditInfoDTO changedProfile = MapperUtils.mapUserToUserEditProfileInfo(
                    userProfileService.changeUserProfileInfo(user, editedProfile)
            );
            return new ResponseEntity<>(changedProfile, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/edit/email")
    public ResponseEntity<String> requestEditEmail(@AuthenticationPrincipal User user,
                                              @RequestBody UserProfileEmailInfoDTO emailInfoDTO) {
        try {
            String msg = userProfileService.requestChangeUserEmail(user, emailInfoDTO.getEmail());
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (UserProfileEditException profileEditException) {
            return new ResponseEntity<>(profileEditException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/edit/email")
    public ResponseEntity<UserProfileEmailInfoDTO> requestEditEmail(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(new UserProfileEmailInfoDTO(user.getEmail()), HttpStatus.OK);
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
}
