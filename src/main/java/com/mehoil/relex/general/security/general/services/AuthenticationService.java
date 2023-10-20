package com.mehoil.relex.general.security.general.services;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.security.general.data.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.general.components.JwtAuthenticationProvider;
import com.mehoil.relex.general.security.general.data.dto.CredentialsDTO;
import com.mehoil.relex.general.security.general.exceptions.UserIsNotEnabledException;

import java.util.Locale;


@Slf4j
@Service
public class AuthenticationService {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;
    private final MessageSource messageSource;

    public AuthenticationService(JwtAuthenticationProvider jwtAuthenticationProvider, UserService userService, MessageSource messageSource) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public LoginDTO authenticateUserByCredentials(CredentialsDTO credentials)
            throws BadCredentialsException, UserIsNotEnabledException, UserNotFoundException {
        User user = jwtAuthenticationProvider.getUserByCredentials(credentials);

        if (!user.isEnabled())
            throw new UserIsNotEnabledException("User " + user.getLogin() + " has tried to login, while not enabled.");

        String loginSuccessMsg = "";
        if (user.isLocked()) {
            userService.restoreUserAccount(user);
            loginSuccessMsg += messageSource.getMessage("user-restored-account", null, Locale.getDefault()) + " ";
            log.info("[AUTH SERVICE] User \"" + user.getLogin() + "\" has restored their account.");
        }
        if (!user.isHasActiveSession()) {
            userService.setActiveSession(user, true);
            userService.setLastOnline(user);
            String token = jwtAuthenticationProvider.createToken(user);
            loginSuccessMsg +=
                            messageSource.getMessage("jwt-token-login-success", null, Locale.getDefault()) + " " +
                            messageSource.getMessage("jwt-token-authentication-reminder", null, Locale.getDefault());
            return new LoginDTO(loginSuccessMsg, token);
        }
        loginSuccessMsg +=
                        messageSource.getMessage("jwt-token-already-received", null, Locale.getDefault()) + " " +
                        messageSource.getMessage("jwt-token-authentication-reminder", null, Locale.getDefault());
        return new LoginDTO(loginSuccessMsg, "-");
    }
}
