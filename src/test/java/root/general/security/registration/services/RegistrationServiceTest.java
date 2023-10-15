package root.general.security.registration.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import root.general.TestUtils;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.main.utils.InfoMessagesUtils;
import root.general.security.general.exceptions.RegistrationException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private EmailConfirmationService emailConfirmationService;
    @Mock
    private RegistrationTokenService registrationTokenService;
    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegisterUserFirstTimeSuccess () throws DatabaseRecordNotFound, RegistrationException {
        User newUser = TestUtils.getNewDefaultUser();
        newUser.setEnabled(false);

        when(userService.getUserByLogin(anyString()))
                .thenThrow(UserNotFoundException.class);
        when(userService.save(any())).thenReturn(newUser);
        when(passwordEncoder.encode(newUser.getPassword()))
                .thenReturn("encodedpassword");
        ignoreStubs(emailConfirmationService);

        String result = registrationService.registerUser(newUser);

        assertEquals(InfoMessagesUtils.registrationSuccessConfirmationLetterSentMsg, result);
        verify(emailConfirmationService, times(1)).sendConfirmationEmail(any(User.class));
    }

    @Test
    void testRegisterUserSecondTimeSuccess () throws DatabaseRecordNotFound, RegistrationException {
        User newUser = TestUtils.getNewDefaultUser();
        newUser.setEnabled(false);

        when(userService.getUserByLogin(anyString()))
                .thenReturn(newUser);

        String result = registrationService.registerUser(newUser);

        assertEquals(InfoMessagesUtils.requestConfirmationLetterAgainMsg, result);
        verify(emailConfirmationService, times(1)).sendConfirmationEmail(any(User.class));
    }

    @Test
    void testRegisterUserThatExistsAndIsEnabledFail () throws DatabaseRecordNotFound, RegistrationException {
        User user = TestUtils.getNewDefaultUser();
        user.setEnabled(true);

        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(user));
    }

    @Test
    void testRegisterUserByExistingLoginFail () throws DatabaseRecordNotFound, RegistrationException {
        User existingUser = TestUtils.getNewDefaultUser();
        existingUser.setEnabled(true);
        User newUser = new User(
                "email@site.com",
                existingUser.getLogin(),
                "pswrd",
                "usrnme",
                "First",
                "Last"
        );

        when(userService.getUserByLogin(existingUser.getLogin()))
                .thenReturn(existingUser);

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(newUser));
    }

    @Test
    void testRegisterUserByExistingEmailFail () throws DatabaseRecordNotFound, RegistrationException {
        User existingUser = TestUtils.getNewDefaultUser();
        existingUser.setEnabled(true);
        User newUser = new User(
                existingUser.getEmail(),
                "uniquelogin",
                "pswrd",
                "usrnme",
                "First",
                "Last"
        );

        when(userService.getUserByLogin(anyString()))
                .thenThrow(UserNotFoundException.class);
        when(userService.getAllUsers())
                .thenReturn(Set.of(existingUser));

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(newUser));
    }

    @Test
    void testConfirmAccountSuccess () throws DatabaseRecordNotFound {
        String registrationToken = "token";
        User registeredUser = TestUtils.getNewDefaultUser();
        registeredUser.setEnabled(false);

        when(registrationTokenService.getUserByRegistrationToken(registrationToken))
                .thenReturn(registeredUser);
        when(userService.save(any(User.class)))
                .thenReturn(any(User.class));

        boolean result = registrationService.confirmAccount(registrationToken);
        assertTrue(registeredUser.isEnabled());
        assertTrue(result);
    }

    @Test
    void testConfirmAccountFail () throws DatabaseRecordNotFound {
        String registrationToken = "token";

        when(registrationTokenService.getUserByRegistrationToken(registrationToken))
                .thenThrow(DatabaseRecordNotFound.class);

        boolean result = registrationService.confirmAccount(registrationToken);
        assertFalse(result);
    }
}