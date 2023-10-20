package com.mehoil.relex.database.services;

import com.mehoil.relex.general.features.community.userprofile.services.UserEmailChangeTokenService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ExpiredTokenChangeEmailCleanupService {

    private final UserEmailChangeTokenService userEmailChangeTokenService;

    public ExpiredTokenChangeEmailCleanupService(UserEmailChangeTokenService userEmailChangeTokenService) {
        this.userEmailChangeTokenService = userEmailChangeTokenService;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime rightNow = LocalDateTime.now();
        userEmailChangeTokenService.getAllTokens()
                .forEach(token -> {
                    if (userEmailChangeTokenService.tokenIsExpiredByDate(token.getToken(), rightNow)) {
                        userEmailChangeTokenService.deleteToken(token);
                    }
                });
    }
}
