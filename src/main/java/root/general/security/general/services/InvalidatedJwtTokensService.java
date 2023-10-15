package root.general.security.general.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.security.general.data.InvalidatedJwtToken;
import root.general.security.general.repositories.InvalidatedJwtTokensRepository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InvalidatedJwtTokensService {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final InvalidatedJwtTokensRepository tokensRepository;
    private final UserService userService;

    public InvalidatedJwtTokensService(InvalidatedJwtTokensRepository tokensRepository, UserService userService) {
        this.tokensRepository = tokensRepository;
        this.userService = userService;
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

            tokensRepository.save(new InvalidatedJwtToken(token));
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
        return tokensRepository.existsById(token);
    }

    public InvalidatedJwtToken getToken(String token) throws DatabaseRecordNotFound {
        return tokensRepository.findById(token)
                .orElseThrow(() -> new DatabaseRecordNotFound("Invalidated token not found: " + token));
    }

    public Set<InvalidatedJwtToken> getAllTokens() {
        return StreamSupport.stream(tokensRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public void deleteToken(InvalidatedJwtToken token){
        tokensRepository.delete(token);
    }

}
