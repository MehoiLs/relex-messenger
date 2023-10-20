package com.mehoil.relex.database.services;

import com.mehoil.relex.general.user.services.UserService;
import org.springframework.stereotype.Service;


@Service
public class DatabaseCleanerService {

    private final ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService;
    private final InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService;
    private final ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService;
    private final UserService userService;

    public DatabaseCleanerService(ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService,
                                  InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService,
                                  ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService,
                                  UserService userService) {
        this.expiredRegistrationTokenCleanupService = expiredRegistrationTokenCleanupService;
        this.invalidatedJwtTokenCleanupService = invalidatedJwtTokenCleanupService;
        this.expiredTokenChangeEmailCleanupService = expiredTokenChangeEmailCleanupService;
        this.userService = userService;
    }

    public void cleanUpDatabase() {
        cleanUpDatabaseFromTokens();
        cleanUpDatabaseFromDeletedUsers();
    }

    private void cleanUpDatabaseFromTokens() {
        expiredRegistrationTokenCleanupService.cleanupExpiredTokens();
        invalidatedJwtTokenCleanupService.cleanupExpiredTokens();
        expiredTokenChangeEmailCleanupService.cleanupExpiredTokens();
    }

    private void cleanUpDatabaseFromDeletedUsers() {
        userService.deleteAllLockedUsers();
    }
}
