package root.main.services;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditInfoDTO;
import root.main.exceptions.ProfilePictureUploadException;
import root.main.exceptions.UserProfileEditException;
import root.main.services.email.EmailTokenChangeService;
import root.main.utils.MapperUtils;
import root.main.utils.MessagesUtils;
import root.main.utils.ValidationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
public class UserProfileService {

    private final UserService userService;
    private final EmailTokenChangeService emailTokenChangeService;
    private final TokenChangeEmailService tokenChangeEmailService;

    @Autowired
    public UserProfileService(UserService userService, EmailTokenChangeService emailTokenChangeService, TokenChangeEmailService tokenChangeEmailService) {
        this.userService = userService;
        this.emailTokenChangeService = emailTokenChangeService;
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    public User changeUserProfileInfo(@NotNull User user,
                                      @NotNull UserProfileEditInfoDTO profileEditInfo) throws UserProfileEditException {
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
            return MessagesUtils.requestConfirmationLetterAgain;
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
}
