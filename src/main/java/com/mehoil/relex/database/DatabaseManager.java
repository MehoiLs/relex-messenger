package com.mehoil.relex.database;

import com.mehoil.relex.database.services.DatabaseCleanerService;
import com.mehoil.relex.database.services.DatabaseUserSessionValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        log("Started cleaning database.");
        dbCleaner.cleanUpDatabase();
        dbUserSessionValidator.handleInactiveUsers();
        log("Finished cleaning database.");
    }

    public static void log(String message) {
        log.info("[DATABASE MANAGER] " + message);
    }

}
