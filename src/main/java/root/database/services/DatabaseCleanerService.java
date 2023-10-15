package root.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.database.DatabaseManager;
import root.general.main.services.user.UserService;


@Service
public class DatabaseCleanerService {

    private final ExpiredRegistrationTokenCleanupService expiredRegistrationTokenCleanupService;
    private final InvalidatedJwtTokenCleanupService invalidatedJwtTokenCleanupService;
    private final ExpiredTokenChangeEmailCleanupService expiredTokenChangeEmailCleanupService;
    private final UserService userService;

    @Autowired
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
        DatabaseManager.log("...Starting cleaning up the database from tokens...");
        expiredRegistrationTokenCleanupService.cleanupExpiredTokens();
        DatabaseManager.log(" Cleaned up all expired registration tokens.");
        invalidatedJwtTokenCleanupService.cleanupExpiredTokens();
        DatabaseManager.log(" Cleaned up all invalidated tokens that have expired.");
        expiredTokenChangeEmailCleanupService.cleanupExpiredTokens();
        DatabaseManager.log(" Cleaned up all expired e-mail change tokens.");
        DatabaseManager.log("...Finished cleaning up the database from tokens...");
    }

    private void cleanUpDatabaseFromDeletedUsers() {
        DatabaseManager.log("...Starting cleaning up the database from deleted (locked) users...");
        userService.deleteAllLockedUsers();
        DatabaseManager.log("...Finished cleaning up the database from deleted (locked) users...");
    }
}
