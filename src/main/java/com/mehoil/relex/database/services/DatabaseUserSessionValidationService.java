package com.mehoil.relex.database.services;

import com.mehoil.relex.general.user.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DatabaseUserSessionValidationService {

    private final UserService userService;

    public DatabaseUserSessionValidationService(UserService userService) {
        this.userService = userService;
    }

    public void handleInactiveUsers() {
        deactivateSessionsOfInactiveUsers();
    }

    @Transactional
    private void deactivateSessionsOfInactiveUsers() {
        userService.getAllUsers().forEach(user -> {
            if(user.getLastOnline().plusDays(1).isBefore(LocalDateTime.now()))
                userService.setActiveSession(user, false);
        });
    }

}
