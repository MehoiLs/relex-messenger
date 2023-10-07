package root.main.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import root.main.services.tokens.ExpiredTokenChangeEmailCleanupService;
import root.security.general.services.InvalidatedJwtTokenCleanupService;
import root.security.registration.services.ExpiredRegistrationTokenCleanupService;

@Slf4j
@Service
public class DatabaseCleanupService {

    private final ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService;
    private final InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService;
    private final ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService;
    private final UserService userService;

    @Autowired
    public DatabaseCleanupService(ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService,
                                  InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService,
                                  ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService,
                                  UserService userService) {
        this.expiredRegistrationTokenCleanupService = expiredRegistrationTokenCleanupService;
        this.invalidatedJwtTokenCleanupService = invalidatedJwtTokenCleanupService;
        this.expiredTokenChangeEmailCleanupService = expiredTokenChangeEmailCleanupService;
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpDatabase() {
        cleanUpDatabaseFromTokens();
        cleanUpDatabaseFromDeletedUsers();
    }

    private void cleanUpDatabaseFromTokens() {
        log.info("[DATABASE CLEANER] ...Starting cleaning up the database from tokens... ");
        expiredRegistrationTokenCleanupService.cleanupExpiredTokens();
        invalidatedJwtTokenCleanupService.cleanupExpiredTokens();
        expiredTokenChangeEmailCleanupService.cleanupExpiredTokens();
        log.info("[DATABASE CLEANER] ...Finished cleaning up the database from tokens... ");
    }

    private void cleanUpDatabaseFromDeletedUsers() {
        log.info("[DATABASE CLEANER] ...Starting cleaning up the database from deleted (locked) users... ");
        userService.deleteAllLockedUsers();
        log.info("[DATABASE CLEANER] ...Finished cleaning up the database from deleted (locked) users... ");
    }
}
