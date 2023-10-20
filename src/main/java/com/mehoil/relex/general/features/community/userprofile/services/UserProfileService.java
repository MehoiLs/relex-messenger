package com.mehoil.relex.general.features.community.userprofile.services;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.features.community.userprofile.exceptions.ProfilePictureUploadException;
import com.mehoil.relex.general.features.community.userprofile.exceptions.UserProfileEditException;
import com.mehoil.relex.general.user.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileEditDTO;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfilePasswordDTO;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.shared.сomponents.UserMapper;
import com.mehoil.relex.shared.utils.ValidationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@Service
public class UserProfileService {

    private final UserService userService;
    private final UserEmailChangeTokenEmailService userEmailChangeTokenEmailService;
    private final UserEmailChangeTokenService userEmailChangeTokenService;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public UserProfileService(UserService userService,
                              UserEmailChangeTokenEmailService userEmailChangeTokenEmailService,
                              UserEmailChangeTokenService userEmailChangeTokenService,
                              MessageSource messageSource,
                              UserMapper userMapper) {
        this.userService = userService;
        this.userEmailChangeTokenEmailService = userEmailChangeTokenEmailService;
        this.userEmailChangeTokenService = userEmailChangeTokenService;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    public User changeUserProfileInfo(@NonNull User user,
                                      @NonNull UserProfileEditDTO profileEditInfo) throws UserProfileEditException {
        if(ValidationUtils.isInvalidFirstNameOrLastName(profileEditInfo.getFirstName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid first name.");
        if(ValidationUtils.isInvalidFirstNameOrLastName(profileEditInfo.getLastName()))
            throw new UserProfileEditException("Couldn't change profile info. Invalid last name.");

        return userService.save(userMapper.mapUserProfileInfoToExistingUser(profileEditInfo, user));
    }

    public void setFriendsListHidden(@NonNull User user, boolean friendsListIsHidden) {
        user.setFriendsListHidden(friendsListIsHidden);
        userService.save(user);
    }

    public void setMessagesFriendsOnly(@NonNull User user, boolean isFriendsOnly) {
        user.setAccessibilityFriendsOnly(isFriendsOnly);
        userService.save(user);
    }

    public String requestChangeUserEmail(@NonNull User user, @NonNull String email)
            throws UserProfileEditException, DatabaseRecordNotFoundException {

        if(userEmailChangeTokenService.tokenExistsForUser(user)) {
            userEmailChangeTokenEmailService.sendConfirmationEmail(user,
                    userEmailChangeTokenService.getEmailFromTokenByUser(user));
            log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                    + email + "\" again.");
            return messageSource.getMessage("confirmation-letter-request-again", null, Locale.getDefault());
        }

        if(user.getEmail().equals(email))
            return messageSource.getMessage("profile-email-change-no-changes", null, Locale.getDefault());

        userEmailChangeTokenEmailService.sendConfirmationEmail(user, email);
        log.info("[USER PROFILE SERVICE] User " + user.getLogin() + " has requested an e-mail change to \""
                + email + "\".");

        return messageSource.getMessage("profile-email-change-letter-sent", null, Locale.getDefault());
    }

    public String uploadUserProfilePicture(@NonNull User user, @NonNull MultipartFile file)
            throws ProfilePictureUploadException {
        try {
            String fileName = file.getOriginalFilename();
            if(fileName == null) throw new ProfilePictureUploadException(
                    messageSource.getMessage("error-file-upload-name-null", null, Locale.getDefault())
            );

            String fileExtension = fileName.substring(fileName.lastIndexOf( '.') + 1).toLowerCase();
            if (file.getContentType() != null &&
                    (!file.getContentType().startsWith("image/") ||
                    (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")))) {
                throw new ProfilePictureUploadException(
                        messageSource.getMessage("error-invalid-image-format", null, Locale.getDefault())
                );
            }

            byte[] fileBytes = file.getBytes();
            user.setProfilePictureBytes(fileBytes);
            userService.save(user);
            return "You have successfully updated your profile picture.";
        } catch (IOException ioException) {
            throw new ProfilePictureUploadException(
                    messageSource.getMessage("error-file-upload", null, Locale.getDefault())
            );
        } catch (NullPointerException nullPointerException) {
            throw new ProfilePictureUploadException(
                    messageSource.getMessage("error-null-pointer", null, Locale.getDefault())
            );
        }
    }

    public ResponseEntity<InputStreamResource> getUserProfilePictureAsInputStreamResource(@NonNull User user) {
        byte[] pfpBytes = user.getProfilePictureBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pfpBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }

    public String changeUserPassword(@NonNull User user, @NonNull UserProfilePasswordDTO userProfilePassword)
            throws UserProfileEditException{
        if(!userService.passwordMatches(userProfilePassword.getOldPassword(), user.getPassword()))
            throw new UserProfileEditException("Your current password is incorrect.");

        user.setPassword(userService.passwordEncode(userProfilePassword.getNewPassword()));
        userService.save(user);
        return messageSource.getMessage("user-change-password-success", null, Locale.getDefault());
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
        return messageSource.getMessage("user-delete-account-request", null, Locale.getDefault());
    }

}
