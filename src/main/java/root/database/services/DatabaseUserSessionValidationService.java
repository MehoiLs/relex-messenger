package root.database.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.database.DatabaseManager;
import root.general.main.services.user.UserService;

import java.time.LocalDateTime;

@Service
public class DatabaseUserSessionValidationService {

    private final UserService userService;

    @Autowired
    public DatabaseUserSessionValidationService(UserService userService) {
        this.userService = userService;
    }

    public void handleInactiveUsers() {
        DatabaseManager.log("Started deactivating sessions of users that have been inactive for more than 24 hours...");
        deactivateSessionsOfInactiveUsers();
        DatabaseManager.log("Finished deactivating sessions.");
    }

    private void deactivateSessionsOfInactiveUsers() {
        userService.getAllUsers().forEach(user -> {
            if(user.getLastOnline().plusDays(1).isBefore(LocalDateTime.now()))
                userService.setActiveSession(user, false);
        });
    }

}
