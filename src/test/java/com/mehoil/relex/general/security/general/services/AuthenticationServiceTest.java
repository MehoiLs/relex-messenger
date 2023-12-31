package com.mehoil.relex.general.security.general.services;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.general.components.JwtAuthenticationProvider;
import com.mehoil.relex.general.security.general.data.dto.CredentialsDTO;
import com.mehoil.relex.general.security.general.exceptions.UserIsNotEnabledException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mehoil.relex.general.TestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private AuthenticationService authenticationService;

    // Пользователь правильно ввёл данные, получил токен и был аутентифицирован
    @Test
    void testAuthenticateEnabledUserByCredentialsSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );
        user.setHasActiveSession(false);
        user.setEnabled(true);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertDoesNotThrow(() ->
                authenticationService.authenticateUserByCredentials(credentials));
        verify(jwtAuthenticationProvider, times(1)).createToken(user);
    }

    // Пользователь уже был аутентифицирован, токен не получил
    @Test
    void testAuthenticateEnabledAndAuthenticatedUserByCredentialsSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );
        user.setHasActiveSession(true);
        user.setEnabled(true);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        assertDoesNotThrow(() ->
                authenticationService.authenticateUserByCredentials(credentials));
        verify(jwtAuthenticationProvider, times(0)).createToken(user);
    }

    // Пользователь ввёл неккоректный логин
    @Test
    void testAuthenticateEnabledUserByCredentialsIncorrectLoginFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                "incorrectlogin",
                user.getPassword()
        );
        user.setHasActiveSession(false);
        user.setEnabled(true);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenThrow(UserNotFoundException.class);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });

        assertThrows(UserNotFoundException.class, () ->
                authenticationService.authenticateUserByCredentials(credentials));
    }

    // Пользователь ввёл неккоректный пароль
    @Test
    void testAuthenticateEnabledUserByCredentialsIncorrectPasswordFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                "wrongpassword"
        );
        user.setHasActiveSession(false);
        user.setEnabled(true);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenThrow(BadCredentialsException.class);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });


        assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticateUserByCredentials(credentials));
    }

    // Пользователь только что зарегистрировался, не подтвердился и попытался залогиниться
    @Test
    void testAuthenticateNonEnabledUserByCredentialsFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );
        user.setHasActiveSession(false);
        user.setEnabled(false);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });

        assertThrows(UserIsNotEnabledException.class, () ->
                authenticationService.authenticateUserByCredentials(credentials));
    }

    // Пользователь запросил удаление аккаунта, но восстановил его
    @Test
    void testAuthenticateLockedUserByCredentialsSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );
        user.setHasActiveSession(false);
        user.setEnabled(true);
        user.setLocked(true);

        when(userService.getUserByLogin(credentials.getLogin()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtAuthenticationProvider.getUserByCredentials(credentials)).thenAnswer(invocation -> {
            CredentialsDTO creds = invocation.getArgument(0);
            User foundUser = userService.getUserByLogin(creds.getLogin());
            if(passwordEncoder.matches(creds.getPassword(), user.getPassword()))
                return foundUser;
            else throw new BadCredentialsException("");
        });
        when(messageSource.getMessage(any(), any(), any())).thenReturn("an info message");

        LocalDateTime rightNow = LocalDateTime.now();
        doAnswer(invocation -> {
            User userToRestore = invocation.getArgument(0);
            userToRestore.setLocked(false);
            user.setLastOnline(rightNow);
            return null;
        }).when(userService).restoreUserAccount(user);

        assertDoesNotThrow(
                () -> authenticationService.authenticateUserByCredentials(credentials));
        verify(userService).restoreUserAccount(user);
    }
}