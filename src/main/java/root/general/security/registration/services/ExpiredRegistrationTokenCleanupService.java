package root.general.security.registration.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.services.user.UserService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ExpiredRegistrationTokenCleanupService {

    private final RegistrationTokenService registrationTokenService;
    private final UserService userService;

    @Autowired
    public ExpiredRegistrationTokenCleanupService(RegistrationTokenService registrationTokenService, UserService userService) {
        this.registrationTokenService = registrationTokenService;
        this.userService = userService;
    }

    public void cleanupExpiredTokens() {
        LocalDateTime rightNow = LocalDateTime.now();
        registrationTokenService.getAllTokens()
                .forEach(token -> {
                    if (registrationTokenService.tokenIsExpiredByDate(token.getToken(), rightNow)) {
                        try {
                            User userToDelete = registrationTokenService.getUserByRegistrationToken(token.getToken());
                            registrationTokenService.deleteToken(token);
                            userService.forceDeleteUser(userToDelete);
                        } catch (Exception ignored) {}
                    }
                });
    }
}
