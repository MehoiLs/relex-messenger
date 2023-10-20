package com.mehoil.relex.general.common.services.user;

import com.mehoil.relex.general.features.community.userprofile.services.UserProfileService;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileEditDTO;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfilePasswordDTO;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.features.community.userprofile.exceptions.ProfilePictureUploadException;
import com.mehoil.relex.general.features.community.userprofile.exceptions.UserProfileEditException;
import com.mehoil.relex.general.features.community.userprofile.services.UserEmailChangeTokenEmailService;
import com.mehoil.relex.general.features.community.userprofile.services.UserEmailChangeTokenService;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.shared.сomponents.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mehoil.relex.general.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserEmailChangeTokenEmailService userEmailChangeTokenEmailService;

    @Mock
    private UserEmailChangeTokenService userEmailChangeTokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private MessageSource messageSource;

    @Mock
    private UserMapper userMapper;

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

        when(userMapper.mapUserProfileInfoToExistingUser(any(UserProfileEditDTO.class), any(User.class)))
                .thenAnswer(invocation -> new UserMapper().mapUserProfileInfoToExistingUser(
                        invocation.getArgument(0), invocation.getArgument(1)
                ));
        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0));

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
            throws UserProfileEditException, DatabaseRecordNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        String newEmail = "newemail@somesite.com";

        when(userEmailChangeTokenService.tokenExistsForUser(user)).thenReturn(false);

        String result = userProfileService.requestChangeUserEmail(user, newEmail);

        verify(userEmailChangeTokenEmailService, times(1))
                .sendConfirmationEmail(any(User.class), anyString());
    }

    @Test
    void testRequestChangeUserEmailAgainSuccess ()
            throws UserProfileEditException, DatabaseRecordNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        String newEmail = "newemail@somesite.com";

        when(userEmailChangeTokenService.tokenExistsForUser(user)).thenReturn(true);
        when(userEmailChangeTokenService.getEmailFromTokenByUser(user)).thenReturn(newEmail);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        String result = userProfileService.requestChangeUserEmail(user, newEmail);

        verify(userEmailChangeTokenEmailService, times(1))
                .sendConfirmationEmail(any(User.class), eq(newEmail));
    }

    @Test
    void testUploadUserProfilePictureSuccess () throws IOException {
        User user = TestUtils.getNewDefaultUser();

        Path imagePath = new ClassPathResource("pictures/profile_pictures/correct_pfp.png").getFile().toPath();
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile mockFile = new MockMultipartFile(
                "pfp", "correct_pfp.png", "image/png", imageBytes);

        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0));
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

        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertThrowsExactly(ProfilePictureUploadException.class,
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
        when(userService.passwordMatches(passwordDTO.getOldPassword(), user.getPassword())).thenReturn(true);
        when(userService.passwordEncode(passwordDTO.getNewPassword())).thenReturn(passwordDTO.getNewPassword());
        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0));

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