package root.main.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import root.security.general.services.InvalidatedJwtTokenCleanupService;
import root.security.registration.services.ExpiredRegistrationTokenCleanupService;

@Slf4j
@Service
public class DatabaseCleanupService {

    private final ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService;
    private final InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService;
    private final ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService;

    @Autowired
    public DatabaseCleanupService(ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService,
                                  InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService,
                                  ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService) {
        this.expiredRegistrationTokenCleanupService = expiredRegistrationTokenCleanupService;
        this.invalidatedJwtTokenCleanupService = invalidatedJwtTokenCleanupService;
        this.expiredTokenChangeEmailCleanupService = expiredTokenChangeEmailCleanupService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpDatabase() {
        log.info("[DATABASE CLEANER] ...Starting cleaning up the database...");
        expiredRegistrationTokenCleanupService.cleanupExpiredTokens();
        invalidatedJwtTokenCleanupService.cleanupExpiredTokens();
        expiredRegistrationTokenCleanupService.cleanupExpiredTokens();
        log.info("[DATABASE CLEANER] ...Finished cleaning up the database...");
    }
}
