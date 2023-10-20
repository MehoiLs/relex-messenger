package com.mehoil.relex.database.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.registration.services.RegistrationTokenService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ExpiredRegistrationTokenCleanupService {

    private final RegistrationTokenService registrationTokenService;
    private final UserService userService;

    public ExpiredRegistrationTokenCleanupService(RegistrationTokenService registrationTokenService, UserService userService) {
        this.registrationTokenService = registrationTokenService;
        this.userService = userService;
    }

    @Transactional
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
