package root.security.general.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import root.main.data.User;
import root.main.data.dto.CredentialsDTO;
import root.main.exceptions.TokenIsInvalidatedException;
import root.main.exceptions.TokenNotFoundException;
import root.main.services.UserService;
import root.security.general.services.InvalidatedJwtTokensService;

import java.util.*;

@Slf4j
@Component
public class JwtAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InvalidatedJwtTokensService tokensService;

    @Autowired
    public JwtAuthenticationProvider(UserService userService,
                                     BCryptPasswordEncoder passwordEncoder,
                                     InvalidatedJwtTokensService tokensService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokensService = tokensService;
    }

    public String createToken(User user) {
        String userId = user.getId().toString();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000*60*60*24));

        return JWT.create()
                .withIssuer(userId)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public Authentication validateToken(String token)
            throws AuthenticationServiceException, TokenNotFoundException, TokenExpiredException, TokenIsInvalidatedException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);

            Long userId = Long.parseLong(decoded.getIssuer());
            User user = userService.getUserById(userId);

            if (tokensService.tokenIsInvalidated(token))
                throw new TokenIsInvalidatedException("Token " + token + " is invalidated.");

            userService.setLastOnline(user);
            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        } catch (TokenExpiredException tokenExpiredException) {
            throw tokenExpiredException;
        } catch (JWTVerificationException verificationException) {
            log.info(verificationException.getMessage());
            throw new AuthenticationServiceException("Token validation failed: " + token);
        }
    }

    public Authentication validateCredentials(CredentialsDTO credentialsDTO) throws BadCredentialsException {
        return new UsernamePasswordAuthenticationToken(
                getUserByCredentials(credentialsDTO), null, Collections.emptyList());
    }

    public User getUserByCredentials(CredentialsDTO credentialsDTO) throws BadCredentialsException {
        String login = credentialsDTO.getLogin();
        String password = credentialsDTO.getPassword();

        User user = userService.getUserByLogin(login);
        if (passwordEncoder.matches(password, user.getPassword())) {
            log.info("[Authentication Provider] A username with login \"" + login + "\" has been validated.");
            return user;
        }
        else throw new BadCredentialsException("Incorrect password");
    }
}
