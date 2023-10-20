package com.mehoil.relex.database.services;

import com.mehoil.relex.general.security.general.services.InvalidatedJwtTokensService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InvalidatedJwtTokenCleanupService {

    private final InvalidatedJwtTokensService tokensService;

    public InvalidatedJwtTokenCleanupService(InvalidatedJwtTokensService tokensService) {
        this.tokensService = tokensService;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokensService.getAllTokens()
                .forEach(token -> {
                    if (tokensService.tokenIsExpired(token))
                        tokensService.deleteToken(token);
                });
    }
}