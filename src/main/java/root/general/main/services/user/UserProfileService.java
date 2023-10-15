package root.general.main.services.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import root.general.main.data.User;
import root.general.main.data.dto.userprofile.UserProfileEditDTO;
import root.general.main.data.dto.userprofile.UserProfilePasswordDTO;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.ProfilePictureUploadException;
import root.general.main.exceptions.UserProfileEditException;
import root.general.main.services.email.EmailTokenChangeService;
import root.general.main.services.tokens.TokenChangeEmailService;
import root.general.main.utils.InfoMessagesUtils;
import root.general.main.utils.MapperUtils;
import root.general.main.utils.ValidationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
public class UserProfileService {

    private final UserService userService;
    private final EmailTokenChangeService emailTokenChangeService;
    private final TokenChangeEmailService tokenChangeEmailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserProfileService(UserService userService,
                              EmailTokenChangeService emailTokenChangeService,
                              TokenChangeEmailService tokenChangeEmailService,
                              BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailTokenChangeService = emailTokenChangeService;
        this.tokenChangeEmailService = tokenChangeEmailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User changeUserProfileInfo(@NonNull User user,
                                      @NonNull UserProfileEditDTO profileEditInfo) throws UserProfileEditException {
        if(!ValidationUtils.isValidFirstNameOrLastName(profileEditInfo.getFirstName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid first name.");
        if(!ValidationUtils.isValidFirstNameOrLastName(profileEditInfo.getLastName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid last name.");

        return userService.save(MapperUtils.mapUserProfileInfoToExistingUser(profileEditInfo, user));
    }

    public User setFriendsListHidden(@NonNull User user, boolean friendsListIsHidden) {
        user.setFriendsListHidden(friendsListIsHidden);
        return userService.save(user);
    }

    public User setMessagesFriendsOnly(@NonNull User user, boolean isFriendsOnly) {
        user.setAccessibilityFriendsOnly(isFriendsOnly);
        return userService.save(user);
    }

    public String requestChangeUserEmail(@NonNull User user, @NonNull String email)
            throws UserProfileEditException, DatabaseRecordNotFound {

        if(tokenChangeEmailService.userHasExistingToken(user)) {
            emailTokenChangeService.sendConfirmationEmail(user,
                    tokenChangeEmailService.getTokenChangeEmailByUser(user));
            log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                    + email + "\" again.");
            return InfoMessagesUtils.requestConfirmationLetterAgainMsg;
        }

        if(!ValidationUtils.isValidEmail(email))
            throw new UserProfileEditException("Couldn't change profile info. Invalid e-mail.");

        if(user.getEmail().equals(email))
            return InfoMessagesUtils.profileEmailChangeCurrentEmailRequestMsg;

        emailTokenChangeService.sendConfirmationEmail(user, email);
        log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                + email + "\".");

        return InfoMessagesUtils.profileEmailChangeConfirmationLetterSentMsg;
    }

    public String uploadUserProfilePicture(@NonNull User user, @NonNull MultipartFile file)
            throws ProfilePictureUploadException {
        try {
            String fileName = file.getOriginalFilename();
            if(fileName == null) throw new ProfilePictureUploadException(InfoMessagesUtils.fileNameIsNullMsg);
            String fileExtension = fileName.substring(fileName.lastIndexOf( '.') + 1).toLowerCase();

            if (file.getContentType() != null &&
                    (!file.getContentType().startsWith("image/") ||
                    (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")))) {
                throw new ProfilePictureUploadException(InfoMessagesUtils.invalidImageFormatMsg);
            }

            byte[] fileBytes = file.getBytes();
            user.setProfilePictureBytes(fileBytes);
            userService.save(user);
            return "You have successfully updated your profile picture.";
        } catch (IOException ioException) {
            throw new ProfilePictureUploadException(InfoMessagesUtils.fileUploadErrorMsg);
        }
    }

    public InputStreamResource getUserProfilePictureAsInputStreamResource(@NonNull User user) {
        byte[] pfpBytes = user.getProfilePictureBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pfpBytes);
        return new InputStreamResource(inputStream);
    }

    public String changeUserPassword(@NonNull User user, @NonNull UserProfilePasswordDTO userProfilePassword)
            throws UserProfileEditException{
        if(!passwordEncoder.matches(userProfilePassword.getOldPassword(), user.getPassword()))
            throw new UserProfileEditException("Your current password is incorrect.");

        user.setPassword(passwordEncoder.encode(userProfilePassword.getNewPassword()));
        userService.save(user);
        return "You have successfully changed your password.";
    }

    public String prepareUserForDelete(@NonNull User user, @NonNull HttpServletRequest request)
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
        return InfoMessagesUtils.userRequestedDeleteAccountMsg;
    }

}
