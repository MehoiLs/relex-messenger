package root.security.general.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.data.dto.CredentialsDTO;
import root.main.exceptions.UserIsNotEnabledException;
import root.main.services.UserService;
import root.main.utils.MessagesUtils;
import root.security.general.components.JwtAuthenticationProvider;

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

        if (!user.isHasActiveSession()) {
            userService.setActiveSession(user, true);
            String token = jwtAuthenticationProvider.createToken(user);
            return "You have successfully logged in.\n" + MessagesUtils.jwtTokenAuthenticationReminderMsg +
                    "\nYour JWT token:\n" + token;
        }
        return "You have already received a JWT token.\n" + MessagesUtils.jwtTokenAuthenticationReminderMsg;
    }

    public void onUserLogout(User user) {
        user.setHasActiveSession(false);
        userService.save(user);
    }
}
