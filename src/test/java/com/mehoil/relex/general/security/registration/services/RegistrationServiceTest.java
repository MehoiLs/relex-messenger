package com.mehoil.relex.general.security.registration.services;

import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.security.general.exceptions.RegistrationException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserConfirmationEmailService userConfirmationEmailService;
    @Mock
    private RegistrationTokenService registrationTokenService;
    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegisterUserFirstTimeSuccess () throws DatabaseRecordNotFoundException, RegistrationException {
        User newUser = TestUtils.getNewDefaultUser();
        newUser.setEnabled(false);

        when(userService.getUserByLogin(anyString()))
                .thenThrow(UserNotFoundException.class);
        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0));
        when(passwordEncoder.encode(newUser.getPassword()))
                .thenReturn("encodedpassword");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");
        ignoreStubs(userConfirmationEmailService);

        String result = registrationService.registerUser(newUser);

        verify(userConfirmationEmailService, times(1)).sendConfirmationEmail(any(User.class));
    }

    @Test
    void testRegisterUserSecondTimeSuccess () throws DatabaseRecordNotFoundException, RegistrationException {
        User newUser = TestUtils.getNewDefaultUser();
        newUser.setEnabled(false);

        when(userService.getUserByLogin(anyString()))
                .thenReturn(newUser);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        String result = registrationService.registerUser(newUser);

        verify(userConfirmationEmailService, times(1)).sendConfirmationEmail(any(User.class));
    }

    @Test
    void testRegisterUserThatExistsAndIsEnabledFail () throws DatabaseRecordNotFoundException, RegistrationException {
        User user = TestUtils.getNewDefaultUser();
        user.setEnabled(true);

        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(user));
    }

    @Test
    void testRegisterUserByExistingLoginFail () throws DatabaseRecordNotFoundException, RegistrationException {
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
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(newUser));
    }

    @Test
    void testRegisterUserByExistingEmailFail () throws DatabaseRecordNotFoundException, RegistrationException {
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
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertThrowsExactly(RegistrationException.class,
                () -> registrationService.registerUser(newUser));
    }

    @Test
    void testConfirmAccountSuccess () throws DatabaseRecordNotFoundException {
        String registrationToken = "token";
        User registeredUser = TestUtils.getNewDefaultUser();
        registeredUser.setEnabled(false);

        when(registrationTokenService.getUserByRegistrationToken(registrationToken))
                .thenReturn(registeredUser);
        when(userService.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertDoesNotThrow(() -> registrationService.confirmAccount(registrationToken));
        assertTrue(registeredUser.isEnabled());
    }

    @Test
    void testConfirmAccountFail () throws DatabaseRecordNotFoundException {
        String registrationToken = "token";

        when(registrationTokenService.getUserByRegistrationToken(registrationToken))
                .thenThrow(TokenNotFoundException.class);

        assertThrowsExactly(TokenNotFoundException.class,
                () -> registrationService.confirmAccount(registrationToken));
    }
}