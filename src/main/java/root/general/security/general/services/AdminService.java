package root.general.security.general.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.database.DatabaseManager;
import root.general.main.data.User;
import root.general.main.services.user.UserService;

@Slf4j
@Service
public class AdminService {

    private final UserService userService;
    private final DatabaseManager databaseManager;

    public AdminService(UserService userService, DatabaseManager databaseManager) {
        this.userService = userService;
        this.databaseManager = databaseManager;
    }

    public void forceLogoutAllUsers (@NonNull User requester) {
        log.info("[ADMIN SERVICE] " + requester.getUsername() + " has forced logout all users.");
        userService.getAllUsers().stream()
                .filter(user -> !user.equals(requester))
                .forEach(user -> userService.setActiveSession(user, false));
    }

    public void forceCleanupDatabase (@NonNull User requester) {
        log.info("[ADMIN SERVICE] " + requester.getUsername() + " has forced cleanup database.");
        databaseManager.handleDatabase();
    }

}
