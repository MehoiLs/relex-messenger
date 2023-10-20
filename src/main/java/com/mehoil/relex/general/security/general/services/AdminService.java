package com.mehoil.relex.general.security.general.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.database.DatabaseManager;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;

import java.util.Locale;

@Slf4j
@Service
public class AdminService {

    private final UserService userService;
    private final DatabaseManager databaseManager;
    private final MessageSource messageSource;

    public AdminService(UserService userService, DatabaseManager databaseManager, MessageSource messageSource) {
        this.userService = userService;
        this.databaseManager = databaseManager;
        this.messageSource = messageSource;
    }

    public String forceLogoutAllUsers (@NonNull User requester) {
        log.info("[ADMIN SERVICE] " + requester.getUsername() + " has forced logout all users.");
        userService.getAllUsers().stream()
                .filter(user -> !user.equals(requester))
                .forEach(user -> userService.setActiveSession(user, false));
        return messageSource.getMessage("admin-force-all-users-logout", null, Locale.getDefault());
    }

    public String forceCleanupDatabase (@NonNull User requester) {
        log.info("[ADMIN SERVICE] " + requester.getUsername() + " has forced cleanup database.");
        databaseManager.handleDatabase();
        return messageSource.getMessage("admin-force-database-cleanup", null, Locale.getDefault());
    }

}
