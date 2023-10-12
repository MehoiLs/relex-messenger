package root.general.security.general.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.security.general.data.dto.CredentialsDTO;
import root.general.security.general.exceptions.UserIsNotEnabledException;
import root.general.main.services.user.UserService;
import root.general.main.utils.InfoMessagesUtils;
import root.general.security.general.components.JwtAuthenticationProvider;


@Slf4j
@Service
public class AuthenticationService {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;

    @Autowired
    public AuthenticationService(JwtAuthenticationProvider jwtAuthenticationProvider,
                                 UserService userService) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.userService = userService;
    }

    public String authenticateUserByCredentials(CredentialsDTO credentials)
            throws BadCredentialsException, UserIsNotEnabledException {
        User user = jwtAuthenticationProvider.getUserByCredentials(credentials);

        if (!user.isEnabled())
            throw new UserIsNotEnabledException("User " + user.getLogin() + " has tried to login, while not enabled.");

        String loginSuccessMsg = "";
        if (user.isLocked()) {
            userService.restoreUserAccount(user);
            loginSuccessMsg += InfoMessagesUtils.userRestoredAccountMsg + "\n";
            log.info("[AUTH SERVICE] User \"" + user.getLogin() + "\" has restored their account.");
        }
        if (!user.isHasActiveSession()) {
            userService.setActiveSession(user, true);
            userService.setLastOnline(user);
            String token = jwtAuthenticationProvider.createToken(user);
            loginSuccessMsg += "You have successfully logged in.\n" + "\nYour JWT token:\n" + token + "\n\n" +
                    InfoMessagesUtils.jwtTokenAuthenticationReminderMsg;
            return loginSuccessMsg;
        }
        return "You have already received a JWT token.\n\n" + InfoMessagesUtils.jwtTokenAuthenticationReminderMsg;
    }
}
