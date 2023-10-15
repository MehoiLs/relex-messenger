package root.general.security.general.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import root.general.TestUtils;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.security.general.data.dto.CredentialsDTO;
import root.general.security.general.exceptions.TokenIsInvalidatedException;
import root.general.security.general.exceptions.TokenNotFoundException;
import root.general.security.general.services.InvalidatedJwtTokensService;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    private final String secretKey = "foreverunknown";
    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private InvalidatedJwtTokensService tokensService;
    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Test
    void testCreateToken() {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String expectedToken = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);
        String result = jwtAuthenticationProvider.createToken(user);

        DecodedJWT decodedExpected = JWT.decode(expectedToken);
        DecodedJWT decodedResult = JWT.decode(result);

        // Будем считать, что разница между сроком истечения ожидаемого токена и
        // полученного должна быть не больше минуты.
        assertTimeout(Duration.ofSeconds(60),
                () -> {
            assertTrue(Math.abs(
                    decodedExpected.getExpiresAt().getTime() -
                            decodedResult.getExpiresAt().getTime()) < 1000, "ExpiresAt differs too much");
        });
        assertEquals(decodedExpected.getIssuer(),
                decodedResult.getIssuer());
        assertEquals(decodedExpected.getClaim("role").asString(),
                decodedResult.getClaim("role").asString());
    }

    @Test
    void testValidateTokenSuccess () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        when(tokensService.tokenIsInvalidated(tokenToValidate)).thenReturn(false);
        when(userService.getUserById(anyLong())).thenReturn(user);
        Authentication result = jwtAuthenticationProvider.validateToken(tokenToValidate);

        assertTrue(result.isAuthenticated());
        assertTrue(result.getAuthorities()
                .contains(new SimpleGrantedAuthority(user.getRole().name())));
        assertInstanceOf(User.class, result.getPrincipal());
    }

    @Test
    void testValidateTokenForNonExistingUserFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User fakeUser = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(fakeUser, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(fakeUser.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", fakeUser.getRole().name())
                .sign(algorithm);

        when(userService.getUserById(anyLong()))
                .thenThrow(UserNotFoundException.class);

        assertThrowsExactly(AuthenticationServiceException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testValidateTokenThatIsInvalidatedFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(tokensService.tokenIsInvalidated(tokenToValidate))
                .thenReturn(true);

        assertThrowsExactly(TokenIsInvalidatedException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testValidateTokenForUserWithInactiveSessionFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(false);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(tokensService.tokenIsInvalidated(tokenToValidate))
                .thenReturn(false);

        assertThrowsExactly(AuthenticationServiceException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testValidateTokenThatIsExpiredFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        Date issuedDayBeforeYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24) * 2);
        Date expiredYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(issuedDayBeforeYesterday)
                .withExpiresAt(expiredYesterday)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        assertThrowsExactly(TokenExpiredException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testValidateTokenThatIsSignedWrongFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256("wrongkey");

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        assertThrowsExactly(AuthenticationServiceException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testValidateTokenTryingToGetWrongClaimFail () throws TokenIsInvalidatedException, TokenNotFoundException, UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String tokenToValidate = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("wrongclaim", "somethingelse")
                .sign(algorithm);

        assertThrowsExactly(AuthenticationServiceException.class,
                () -> jwtAuthenticationProvider.validateToken(tokenToValidate));
    }

    @Test
    void testGetUserByCredentialsSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );

        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);
        User result = jwtAuthenticationProvider.getUserByCredentials(credentials);

        assertEquals(user, result);
    }

    @Test
    void testGetUserByCredentialsWrongPasswordFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                "wrongpassword"
        );

        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrowsExactly(BadCredentialsException.class,
                () -> jwtAuthenticationProvider.getUserByCredentials(credentials));
    }

    @Test
    void testGetUserByCredentialsWrongLoginFail () throws UserNotFoundException {
        CredentialsDTO credentials = new CredentialsDTO(
                "nonexistinglogin",
                "password"
        );
        when(userService.getUserByLogin(anyString()))
                .thenThrow(UserNotFoundException.class);

        assertThrowsExactly(UserNotFoundException.class,
                () -> jwtAuthenticationProvider.getUserByCredentials(credentials));
    }

    @Test
    void testValidateCredentialsSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                user.getPassword()
        );
        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(true);

        Authentication result = jwtAuthenticationProvider.validateCredentials(credentials);

        assertTrue(result.isAuthenticated());
        assertTrue(result.getAuthorities()
                .contains(new SimpleGrantedAuthority(user.getRole().name())));
        assertInstanceOf(User.class, result.getPrincipal());
    }

    @Test
    void testValidateCredentialsWrongLoginFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                "wrong",
                "password"
        );
        when(userService.getUserByLogin(anyString()))
                .thenThrow(UserNotFoundException.class);

        assertThrowsExactly(UserNotFoundException.class,
                () -> jwtAuthenticationProvider.validateCredentials(credentials));
    }

    @Test
    void testValidateCredentialsWrongPasswordFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        CredentialsDTO credentials = new CredentialsDTO(
                user.getLogin(),
                "wrongpassword"
        );
        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);
        when(passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrowsExactly(BadCredentialsException.class,
                () -> jwtAuthenticationProvider.validateCredentials(credentials));
    }

    @Test
    void testGetUserOrNullByTokenSuccess () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(userService.getUserByAuth(any()))
                .thenReturn(user);
        when(tokensService.tokenIsInvalidated(token))
                .thenReturn(false);

        User result = jwtAuthenticationProvider.getUserOrNullByToken(token);
        assertEquals(user, result);
    }

    @Test
    void testGetUserOrNullByTokenThatIsExpiredFail () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date issuedDayBeforeYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24) * 2);
        Date expiredYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(issuedDayBeforeYesterday)
                .withExpiresAt(expiredYesterday)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        assertNull(jwtAuthenticationProvider.getUserOrNullByToken(token));
    }

    @Test
    void testGetUserOrNullByTokenThatIsNullFail () {
        assertNull(jwtAuthenticationProvider.getUserOrNullByToken(null));
    }

    @Test
    void testCreateCookieByTokenSuccess () {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        Cookie result = jwtAuthenticationProvider.createCookieByToken(token);

        assertEquals("accessToken", result.getName());
        assertTrue(result.isHttpOnly());
        assertEquals("/", result.getPath());

        long maxAgeSeconds = (expiresAt.getTime() - System.currentTimeMillis()) / 1000;
        assertTrue(result.getMaxAge() - maxAgeSeconds < 5);
    }

}