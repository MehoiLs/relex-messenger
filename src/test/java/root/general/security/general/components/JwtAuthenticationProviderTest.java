package root.general.security.general.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import root.general.TestUtils;
import root.general.main.data.User;
import root.general.main.services.user.UserService;
import root.general.security.general.services.InvalidatedJwtTokensService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void createToken() {
        User user = TestUtils.getNewDefaultUser();
        ReflectionTestUtils.setField(user, "id", 123L);

        // TODO
    }

    @Test
    void validateToken() {
    }

    @Test
    void validateCredentials() {
    }

    @Test
    void getUserByCredentials() {
    }

    @Test
    void getUserOrNullByToken() {
    }

    @Test
    void createCookieByToken() {
    }

}