package root.security.general.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import root.main.services.UserService;
import root.security.registration.services.RegistrationTokenService;

import java.util.Date;

@Slf4j
@Service
public class InvalidatedJwtTokenCleanupService {

    private final InvalidatedJwtTokensService tokensService;

    @Autowired
    public InvalidatedJwtTokenCleanupService(InvalidatedJwtTokensService tokensService) {
        this.tokensService = tokensService;
    }

    public void cleanupExpiredTokens() {
        tokensService.getAllTokens()
                .forEach(token -> {
                    if (tokensService.tokenIsExpired(token))
                        tokensService.deleteToken(token);
                });
        log.info("[DATABASE CLEANER] All expired invalidated JWT tokens have been deleted from the database.");
    }
}
