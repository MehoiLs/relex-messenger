package root.security.registration.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.services.UserService;

import java.util.Date;

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

    //TODO FIX

    @Scheduled(cron = "0 0 0 * * ?") // every midnight
    public void cleanupExpiredTokens() {
        Date rightNow = new Date();
        registrationTokenService.getAllTokens()
                .forEach(token -> {
                    if (registrationTokenService.tokenIsExpiredByDate(token.getToken(), rightNow)) {
                        User userToDelete = registrationTokenService.getUserByRegistrationToken(token.getToken());
                        registrationTokenService.deleteToken(token);
                        userService.deleteUser(userToDelete);
                    }
                });
        log.info("[CLEANER] All expired registration tokens and non-enabled users have been deleted from the database.");
    }
}
