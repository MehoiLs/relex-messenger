package root.database.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.main.services.tokens.TokenChangeEmailService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ExpiredTokenChangeEmailCleanupService {

    private final TokenChangeEmailService tokenChangeEmailService;

    @Autowired
    public ExpiredTokenChangeEmailCleanupService(TokenChangeEmailService tokenChangeEmailService) {
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    public void cleanupExpiredTokens() {
        LocalDateTime rightNow = LocalDateTime.now();
        tokenChangeEmailService.getAllTokens()
                .forEach(token -> {
                    if (tokenChangeEmailService.tokenIsExpiredByDate(token.getToken(), rightNow)) {
                        tokenChangeEmailService.deleteToken(token);
                    }
                });
    }
}
