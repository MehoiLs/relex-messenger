package root.database.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.security.general.services.InvalidatedJwtTokensService;

@Slf4j
@Service
public class InvalidatedJwtTokenCleanupService {

    private final InvalidatedJwtTokensService tokensService;

    public InvalidatedJwtTokenCleanupService(InvalidatedJwtTokensService tokensService) {
        this.tokensService = tokensService;
    }

    public void cleanupExpiredTokens() {
        tokensService.getAllTokens()
                .forEach(token -> {
                    if (tokensService.tokenIsExpired(token))
                        tokensService.deleteToken(token);
                });
    }
}
