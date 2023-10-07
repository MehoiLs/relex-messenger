package root.main.services.tokens;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ExpiredTokenChangeEmailCleanupService {

    private final TokenChangeEmailService tokenChangeEmailService;

    @Autowired
    public ExpiredTokenChangeEmailCleanupService(TokenChangeEmailService tokenChangeEmailService) {
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    public void cleanupExpiredTokens() {
        Date rightNow = new Date();
        tokenChangeEmailService.getAllTokens()
                .forEach(token -> {
                    if (tokenChangeEmailService.tokenIsExpiredByDate(token.getToken(), rightNow)) {
                        tokenChangeEmailService.deleteToken(token);
                    }
                });
        log.info("[DATABASE CLEANER] All expired e-mail change tokens have been deleted from the database.");
    }
}
