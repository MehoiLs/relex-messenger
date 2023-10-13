package root.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import root.database.services.DatabaseCleanerService;
import root.database.services.DatabaseUserSessionValidationService;

@Slf4j
@Service
public class DatabaseManager {

    private final DatabaseCleanerService dbCleaner;
    private final DatabaseUserSessionValidationService dbUserSessionValidator;

    public DatabaseManager(DatabaseCleanerService dbCleaner, DatabaseUserSessionValidationService dbUserSessionValidator) {
        this.dbCleaner = dbCleaner;
        this.dbUserSessionValidator = dbUserSessionValidator;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void handleDatabase() {
        dbCleaner.cleanUpDatabase();
        dbUserSessionValidator.handleInactiveUsers();
    }

    public static void log(String message) {
        log.info("[DATABASE MANAGER] " + message);
    }

}
