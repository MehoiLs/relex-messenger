package com.mehoil.relex.general.security.general.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mehoil.relex.general.security.general.repositories.InvalidatedJwtTokensRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.general.data.InvalidatedJwtToken;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InvalidatedJwtTokensService {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final InvalidatedJwtTokensRepository tokensRepository;
    private final UserService userService;
    private final MessageSource messageSource;

    public InvalidatedJwtTokensService(InvalidatedJwtTokensRepository tokensRepository, UserService userService, MessageSource messageSource) {
        this.tokensRepository = tokensRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public void invalidateToken(String token) {
        if(token == null) return;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);

            Long userId = Long.parseLong(decoded.getIssuer());
            User user = userService.getUserById(userId);
            userService.setActiveSession(user, false);

            tokensRepository.save(new InvalidatedJwtToken(token, decoded.getExpiresAt()));
        } catch (JWTVerificationException | UserNotFoundException ignored) {}
    }

    public boolean tokenIsExpired(InvalidatedJwtToken token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token.getToken());
        } catch (TokenExpiredException tokenExpiredException) {
            return true;
        }
        return false;
    }

    public boolean tokenIsInvalidated(String token) {
        return tokensRepository.existsByToken(token);
    }

    public InvalidatedJwtToken getToken(String token) throws DatabaseRecordNotFoundException {
        return tokensRepository.findByToken(token)
                .orElseThrow(() -> new DatabaseRecordNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public Set<InvalidatedJwtToken> getAllTokens() {
        return new HashSet<>(tokensRepository.findAll());
    }

    public void deleteToken(InvalidatedJwtToken token){
        tokensRepository.delete(token);
    }

}
