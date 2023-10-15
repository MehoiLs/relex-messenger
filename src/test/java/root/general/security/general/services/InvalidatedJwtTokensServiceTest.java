package root.general.security.general.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import root.general.TestUtils;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.security.general.data.InvalidatedJwtToken;
import root.general.security.general.repositories.InvalidatedJwtTokensRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvalidatedJwtTokensServiceTest {

    private final String secretKey = "foreverunknown";
    @Mock
    private InvalidatedJwtTokensRepository tokensRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private InvalidatedJwtTokensService invalidatedJwtTokensService;

    @Test
    void testInvalidateToken () throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                invalidatedJwtTokensService, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        doAnswer(invocation -> {
            User givenUser = invocation.getArgument(0);
            boolean isActive = invocation.getArgument(1);
            givenUser.setHasActiveSession(isActive);
            return null;
        }).when(userService).setActiveSession(any(), anyBoolean());

        invalidatedJwtTokensService.invalidateToken(token);
        assertFalse(user.isHasActiveSession());
    }

    @Test
    void testTokenIsExpiredTrue () {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date issuedDayBeforeYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24) * 2);
        Date expiredYesterday =
                new Date(new Date().getTime() - (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                invalidatedJwtTokensService, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(issuedDayBeforeYesterday)
                .withExpiresAt(expiredYesterday)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        assertTrue(invalidatedJwtTokensService.tokenIsExpired(
                new InvalidatedJwtToken(token)));
    }

    @Test
    void testTokenIsExpiredFalse () {
        User user = TestUtils.getNewDefaultUser();
        user.setHasActiveSession(true);
        ReflectionTestUtils.setField(user, "id", 123L);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        ReflectionTestUtils.setField(
                invalidatedJwtTokensService, "secretKey", secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = JWT.create()
                .withIssuer(user.getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", user.getRole().name())
                .sign(algorithm);

        assertFalse(invalidatedJwtTokensService.tokenIsExpired(
                new InvalidatedJwtToken(token)));
    }

    @Test
    void testTokenIsInvalidatedTrue () {
        when(tokensRepository.existsById("token"))
                .thenReturn(true);
        assertTrue(invalidatedJwtTokensService.tokenIsInvalidated("token"));
    }

    @Test
    void testTokenIsInvalidatedFalse () {
        when(tokensRepository.existsById("token"))
                .thenReturn(false);
        assertFalse(invalidatedJwtTokensService.tokenIsInvalidated("token"));
    }

    @Test
    void testGetTokenSuccess () throws DatabaseRecordNotFound {
        String token = "token";
        InvalidatedJwtToken invalidatedJwtToken = new InvalidatedJwtToken(token);
        when(tokensRepository.findById(token))
                .thenReturn(Optional.of(invalidatedJwtToken));
        assertEquals(invalidatedJwtToken,
                invalidatedJwtTokensService.getToken(token));
    }

    @Test
    void testGetTokenFail () {
        String token = "faketoken";
        when(tokensRepository.findById(token))
                .thenReturn(Optional.empty());
        assertThrowsExactly(DatabaseRecordNotFound.class,
                () -> invalidatedJwtTokensService.getToken(token));
    }

    @Test
    void testGetAllTokens() {
        List<InvalidatedJwtToken> fakeRepository = List.of(
                new InvalidatedJwtToken("token1"),
                new InvalidatedJwtToken("token2")
        );
        when(tokensRepository.findAll()).thenReturn(fakeRepository);
        Set<InvalidatedJwtToken> result = invalidatedJwtTokensService.getAllTokens();
        assertTrue(result.containsAll(fakeRepository));
    }
}