package root.main.services.community;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditDTO;
import root.main.data.dto.userprofile.UserProfilePasswordDTO;
import root.main.exceptions.ProfilePictureUploadException;
import root.main.exceptions.UserProfileEditException;
import root.main.services.UserService;
import root.main.services.email.EmailTokenChangeService;
import root.main.services.tokens.TokenChangeEmailService;
import root.main.utils.MapperUtils;
import root.main.utils.MessagesUtils;
import root.main.utils.ValidationUtils;
import root.security.general.components.CustomLogoutHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
public class UserProfileService {

    private final UserService userService;
    private final EmailTokenChangeService emailTokenChangeService;
    private final TokenChangeEmailService tokenChangeEmailService;
    private final CustomLogoutHandler logoutHandler;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserProfileService(UserService userService,
                              EmailTokenChangeService emailTokenChangeService,
                              TokenChangeEmailService tokenChangeEmailService,
                              CustomLogoutHandler logoutHandler,
                              BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailTokenChangeService = emailTokenChangeService;
        this.tokenChangeEmailService = tokenChangeEmailService;
        this.logoutHandler = logoutHandler;
        this.passwordEncoder = passwordEncoder;
    }

    public User changeUserProfileInfo(@NotNull User user,
                                      @NotNull UserProfileEditDTO profileEditInfo) throws UserProfileEditException {
        if(!ValidationUtils.isValidFirstNameOrLastName(profileEditInfo.getFirstName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid first name.");
        if(!ValidationUtils.isValidFirstNameOrLastName(profileEditInfo.getLastName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid last name.");

        return userService.save(MapperUtils.mapUserProfileInfoToExistingUser(profileEditInfo, user));
    }

    public String requestChangeUserEmail(@NotNull User user, @NotNull String email) throws UserProfileEditException {

        if(tokenChangeEmailService.userHasExistingToken(user)) {
            emailTokenChangeService.sendConfirmationEmail(user, email);
            log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                    + email + "\" again.");
            return MessagesUtils.requestConfirmationLetterAgainMsg;
        }

        if(!ValidationUtils.isValidEmail(email))
            throw new UserProfileEditException("Couldn't change profile info. Invalid e-mail.");

        emailTokenChangeService.sendConfirmationEmail(user, email);
        log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                + email + "\".");

        return MessagesUtils.profileEmailChangeConfirmationLetterSentMsg;
    }

    public String uploadUserProfilePicture(@NotNull User user, @NotNull MultipartFile file)
            throws ProfilePictureUploadException {
        try {
            String fileName = file.getOriginalFilename();
            if(fileName == null) throw new ProfilePictureUploadException(MessagesUtils.fileNameIsNullMsg);
            String fileExtension = fileName.substring(fileName.lastIndexOf( '.') + 1).toLowerCase();

            if (file.getContentType() != null &&
                    (!file.getContentType().startsWith("image/") ||
                    (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")))) {
                throw new ProfilePictureUploadException(MessagesUtils.invalidImageFormatMsg);
            }

            byte[] fileBytes = file.getBytes();
            user.setProfilePictureBytes(fileBytes);
            userService.save(user);
            return "You have successfully updated your profile picture.";
        } catch (IOException ioException) {
            throw new ProfilePictureUploadException(MessagesUtils.fileUploadErrorMsg);
        }
    }

    public InputStreamResource getUserProfilePictureAsInputStreamResource(@NotNull User user) {
        byte[] pfpBytes = user.getProfilePictureBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pfpBytes);
        return new InputStreamResource(inputStream);
    }

    public String changeUserPassword(@NotNull User user, @NotNull UserProfilePasswordDTO userProfilePassword)
            throws UserProfileEditException{
        if(!passwordEncoder.matches(userProfilePassword.getOldPassword(), user.getPassword()))
            throw new UserProfileEditException("Your current password is incorrect.");

        user.setPassword(passwordEncoder.encode(userProfilePassword.getNewPassword()));
        userService.save(user);
        return "You have successfully changed your password.";
    }

    public String prepareUserForDelete(@NotNull User user, @NotNull HttpServletRequest request)
            throws UserProfileEditException {
        user.setLocked(true);
        user.setHasActiveSession(false);
        try {
            request.logout();
        } catch (ServletException e) {
            log.info("[USER PROFILE SERVICE] Error while preparing user: \"" + user.getLogin() + "\" for deleting.");
            throw new UserProfileEditException("Could not process deleting your account.");
        }
        userService.save(user);
        log.info("[USER PROFILE SERVICE] Prepared user: \"" + user.getLogin() + "\" for deleting.");
        return "You have successfully deleted your account. However, it will remain deactivated for " +
                "7 days, before it is deleted permanently. In case you want to restore your account, " +
                "you will have to login with your credentials again.";
    }

}
