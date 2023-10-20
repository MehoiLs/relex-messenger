package com.mehoil.relex.general.security.general.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.general.exceptions.TokenIsInvalidatedException;
import com.mehoil.relex.general.security.general.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.security.general.services.InvalidatedJwtTokensService;
import com.mehoil.relex.shared.utils.AppUtils;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.security.general.data.dto.CredentialsDTO;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class JwtAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InvalidatedJwtTokensService tokensService;
    private final MessageSource messageSource;

    public JwtAuthenticationProvider(UserService userService,
                                     BCryptPasswordEncoder passwordEncoder,
                                     InvalidatedJwtTokensService tokensService,
                                     MessageSource messageSource) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokensService = tokensService;
        this.messageSource = messageSource;
    }

    public String createToken(User user) {
        String userId = user.getId().toString();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Date now = new Date();
        Date expiresAt = AppUtils.getCurrentDatePlusOneDay();

        return JWT.create()
                .withIssuer(userId)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
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

            if (tokensService.tokenIsInvalidated(token)) {
                deactivateUserSessionByToken(token);
                throw new TokenIsInvalidatedException("Token is invalidated.");
            }

            if (!user.isHasActiveSession()) {
                tokensService.invalidateToken(token);
                throw new AuthenticationServiceException("User is logged out.");
            }

            userService.setLastOnline(user);

            String role = decoded.getClaim("role").asString();
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        } catch (TokenExpiredException tokenExpiredException) {
            deactivateUserSessionByToken(token);
            throw tokenExpiredException;
        } catch (JWTVerificationException verificationException) {
            deactivateUserSessionByToken(token);
            log.info(verificationException.getMessage());
            throw new AuthenticationServiceException("Could not verify the provided token.");
        } catch (UserNotFoundException userNotFoundException) {
            throw new AuthenticationServiceException("Could not find the issuer of the token");
        } catch (NullPointerException nullPointerException) {
            throw new AuthenticationServiceException("Error while verifying the token. " +
                    "Probably tried to get inappropriate claims.");
        }
    }

    public Authentication validateCredentials(CredentialsDTO credentialsDTO)
            throws BadCredentialsException, UserNotFoundException {
        User user = getUserByCredentials(credentialsDTO);
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        return new UsernamePasswordAuthenticationToken(
                user, null, authorities);
    }

    public User getUserByCredentials(CredentialsDTO credentialsDTO) throws BadCredentialsException, UserNotFoundException {
        String login = credentialsDTO.getLogin();
        String password = credentialsDTO.getPassword();

        User user = userService.getUserByLogin(login);
        if (passwordEncoder.matches(password, user.getPassword())) {
            if (!user.isHasActiveSession())
                log.info("[Authentication Provider] A username with login \"" + login + "\" has been validated.");
            return user;
        }
        else throw new BadCredentialsException(
                messageSource.getMessage("credentials-incorrect-password", null, Locale.getDefault())
        );
    }

    public User getUserOrNullByToken(String token) {
        if(token == null) return null;
        try {
            Authentication auth = validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return userService.getUserByAuth(auth);
        } catch (Exception e) {
            return null;
        }
    }

    public Cookie createCookieByToken(String token) {
        DecodedJWT decoded = JWT.decode(token);
        long maxAgeSeconds = (decoded.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000;
        if(maxAgeSeconds < 0) maxAgeSeconds = 1;

        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) maxAgeSeconds);
        cookie.setPath("/");

        return cookie;
    }

    public void deactivateUserSessionByToken(String token) {
        try {
            DecodedJWT decoded = JWT.decode(token);
            User user = userService.getUserById(Long.parseLong(decoded.getIssuer()));
            userService.setActiveSession(user, false);
        }
        catch (JWTDecodeException | UserNotFoundException ignored) {}
    }
}
