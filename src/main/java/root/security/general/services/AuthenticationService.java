package root.security.general.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.data.dto.CredentialsDTO;
import root.main.exceptions.UserIsNotEnabledException;
import root.main.services.UserService;
import root.main.utils.MessagesUtils;
import root.security.general.components.JwtAuthenticationProvider;


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
            loginSuccessMsg += MessagesUtils.userRestoredAccountMsg + "\n";
            log.info("[AUTH SERVICE] User \"" + user.getLogin() + "\" has restored their account.");
        }
        if (!user.isHasActiveSession()) {
            userService.setActiveSession(user, true);
            userService.setLastOnline(user);
            String token = jwtAuthenticationProvider.createToken(user);
            loginSuccessMsg += "You have successfully logged in.\n" + "\nYour JWT token:\n" + token + "\n\n" +
                    MessagesUtils.jwtTokenAuthenticationReminderMsg;
            return loginSuccessMsg;
        }
        return "You have already received a JWT token.\n\n" + MessagesUtils.jwtTokenAuthenticationReminderMsg;
    }
}
