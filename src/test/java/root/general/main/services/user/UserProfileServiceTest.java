package root.general.main.services.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import root.general.TestUtils;
import root.general.main.data.User;
import root.general.main.data.dto.userprofile.UserProfileEditDTO;
import root.general.main.data.dto.userprofile.UserProfilePasswordDTO;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.ProfilePictureUploadException;
import root.general.main.exceptions.UserProfileEditException;
import root.general.main.services.email.EmailTokenChangeService;
import root.general.main.services.tokens.TokenChangeEmailService;
import root.general.main.utils.InfoMessagesUtils;
import root.general.main.utils.ValidationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailTokenChangeService emailTokenChangeService;

    @Mock
    private TokenChangeEmailService tokenChangeEmailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void testChangeUserProfileCorrectData() throws UserProfileEditException {
        User user = TestUtils.getNewDefaultUser();
        UserProfileEditDTO profileEditInfo = new UserProfileEditDTO(
                "newUsername",
                "newStatus",
                "newDescription",
                "Newfirstname",
                "Newlastname"
        );

        when(userService.save(any())).thenReturn(user);

        User result = userProfileService.changeUserProfileInfo(user, profileEditInfo);

        assertEquals(profileEditInfo.getFirstName(), result.getFirstName());
        assertEquals(profileEditInfo.getLastName(), result.getLastName());
        assertEquals(profileEditInfo.getUsername(), result.getUsername());
        assertEquals(profileEditInfo.getPersonalStatus(), result.getPersonalStatus());
        assertEquals(profileEditInfo.getDescription(), result.getDescription());
    }

    @Test
    void testChangeUserProfileIncorrectData() {
        User user = TestUtils.getNewDefaultUser();
        UserProfileEditDTO profileEditInfo = new UserProfileEditDTO(
                "new1Usern2am3e",
                "newStatus",
                "newDescription",
                "Ne1wfir3stname",
                "Newlas!tname"
        );

        assertThrows(UserProfileEditException.class,
                () -> userProfileService.changeUserProfileInfo(user, profileEditInfo));
    }

    @Test
    void testRequestChangeUserEmailSuccess ()
            throws UserProfileEditException, DatabaseRecordNotFound {
        User user = TestUtils.getNewDefaultUser();
        String newEmail = "newemail@somesite.com";

        when(tokenChangeEmailService.userHasExistingToken(user)).thenReturn(false);

        String result = userProfileService.requestChangeUserEmail(user, newEmail);

        assertEquals(InfoMessagesUtils.profileEmailChangeConfirmationLetterSentMsg, result);
    }

    @Test
    void testRequestChangeUserEmailAgainSuccess ()
            throws UserProfileEditException, DatabaseRecordNotFound {
        User user = TestUtils.getNewDefaultUser();
        String newEmail = "newemail@somesite.com";

        when(tokenChangeEmailService.userHasExistingToken(user)).thenReturn(true);

        String result = userProfileService.requestChangeUserEmail(user, newEmail);

        assertEquals(InfoMessagesUtils.requestConfirmationLetterAgainMsg, result);
    }

    @Test
    void testRequestChangeUserEmailFail () {
        User user = TestUtils.getNewDefaultUser();
        String newEmail = "newemailsomesite.com";

        when(tokenChangeEmailService.userHasExistingToken(user)).thenReturn(false);

        assertThrows(UserProfileEditException.class,
                () -> userProfileService.requestChangeUserEmail(user, newEmail));
    }

    @Test
    void testUploadUserProfilePictureSuccess () throws IOException {
        User user = TestUtils.getNewDefaultUser();

        Path imagePath = new ClassPathResource("pictures/profile_pictures/correct_pfp.png").getFile().toPath();
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile mockFile = new MockMultipartFile(
                "pfp", "correct_pfp.png", "image/png", imageBytes);

        when(userService.save(any())).thenReturn(user);
        assertDoesNotThrow(() -> userProfileService.uploadUserProfilePicture(user, mockFile));
        assertEquals(user.getProfilePictureBytes(), imageBytes);
    }

    @Test
    void testUploadUserProfilePictureFail () throws IOException {
        User user = TestUtils.getNewDefaultUser();

        Path imagePath = new ClassPathResource("pictures/profile_pictures/correct_pfp.png").getFile().toPath();
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile mockFile = new MockMultipartFile(
                "pfp", "incorrect_pfp.tga", "image/png", imageBytes);

        assertThrows(ProfilePictureUploadException.class,
                () -> userProfileService.uploadUserProfilePicture(user, mockFile));
    }

    @Test
    void testChangeUserPasswordSuccess () {
        User user = TestUtils.getNewDefaultUser();
        String newPassword = "newPassword!";
        UserProfilePasswordDTO passwordDTO = new UserProfilePasswordDTO(
                user.getPassword(),
                newPassword
        );
        when(passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordDTO.getNewPassword())).thenReturn(passwordDTO.getNewPassword());
        when(userService.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userProfileService.changeUserPassword(user, passwordDTO));
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testChangeUserPasswordFail () {
        User user = TestUtils.getNewDefaultUser();
        String newPassword = "newPassword!";
        UserProfilePasswordDTO passwordDTO = new UserProfilePasswordDTO(
                "incorrectpassword",
                newPassword
        );
        when(passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UserProfileEditException.class,
                () -> userProfileService.changeUserPassword(user, passwordDTO));
    }

    @Test
    void testPrepareUserForDeleteSuccess() throws ServletException {
        User user = TestUtils.getNewDefaultUser();
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertDoesNotThrow(() -> userProfileService.prepareUserForDelete(user, request));

        assertTrue(user.isLocked());
        assertFalse(user.isHasActiveSession());
        verify(request).logout();
        verify(userService).save(user);
    }
}