package com.mehoil.relex.general.security.registration.services;

import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.security.registration.data.RegistrationToken;
import com.mehoil.relex.general.security.registration.repositories.RegistrationTokensRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationTokenServiceTest {

    @Mock
    private RegistrationTokensRepository registrationTokensRepository;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private RegistrationTokenService registrationTokenService;

    @Test
    void testGetAllTokens() {
        List<RegistrationToken> tokens = getNewTokens();
        when(registrationTokensRepository.findAll())
                .thenReturn(tokens);

        Set<RegistrationToken> result = registrationTokenService.getAllTokens();
        assertTrue(result.containsAll(tokens));
    }

    @Test
    void testGetTokenSuccess () throws DatabaseRecordNotFoundException {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        when(registrationTokensRepository.findById(token1.getToken()))
                .thenReturn(Optional.of(token1));

        RegistrationToken result = registrationTokenService.getToken(token1.getToken());
        assertEquals(result, token1);
    }

    @Test
    void testGetTokenFail ()  {
        List<RegistrationToken> tokens = getNewTokens();
        when(registrationTokensRepository.findById("faketoken"))
                .thenReturn(Optional.empty());

        assertThrowsExactly(TokenNotFoundException.class,
                () -> registrationTokenService.getToken("faketoken"));
    }

    @Test
    void testGetUserByRegistrationTokenSuccess () throws DatabaseRecordNotFoundException {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        User user = token1.getUser();
        when(registrationTokensRepository.findById(token1.getToken()))
                .thenReturn(Optional.of(token1));

        User result =
                registrationTokenService.getUserByRegistrationToken(token1.getToken());
        assertEquals(user, result);
    }

    @Test
    void testGetUserByRegistrationTokenFail () {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        when(registrationTokensRepository.findById(token1.getToken()))
                .thenReturn(Optional.empty());

        assertThrowsExactly(TokenNotFoundException.class,
                () -> registrationTokenService.getUserByRegistrationToken(token1.getToken()));
    }

    @Test
    void testGetRegistrationTokenByUserSuccess () throws DatabaseRecordNotFoundException {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        User user = token1.getUser();
        when(registrationTokensRepository.findByUser(user))
                .thenReturn(Optional.of(token1));

        String result =
                registrationTokenService.getRegistrationTokenByUser(user);
        assertEquals(token1.getToken(), result);
    }

    @Test
    void testGetRegistrationTokenByUserFail ()  {
        User nonRelatedUser = TestUtils.getNewUsers().get(2);
        when(registrationTokensRepository.findByUser(nonRelatedUser))
                .thenReturn(Optional.empty());

        assertThrowsExactly(TokenNotFoundException.class,
                () -> registrationTokenService.getRegistrationTokenByUser(nonRelatedUser));
    }

    @Test
    void testTokenExistsForUserSuccess () {
        User nonRelatedUser = TestUtils.getNewUsers().get(2);
        when(registrationTokensRepository.findByUser(nonRelatedUser))
                .thenReturn(Optional.empty());

        assertFalse(registrationTokenService.tokenExistsForUser(nonRelatedUser));
    }

    @Test
    void testTokenExistsForUserFail () {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        User user = token1.getUser();
        when(registrationTokensRepository.findByUser(user))
                .thenReturn(Optional.of(token1));

        assertTrue(registrationTokenService.tokenExistsForUser(user));
    }

    @Test
    void testTokenIsExpiredByDateWhenOneDayPassed () {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        when(registrationTokensRepository.findById(token1.getToken()))
                .thenReturn(Optional.of(token1));

        LocalDateTime oneDayAfterRightNow = LocalDateTime.now().plusDays(1).plusMinutes(1);
        assertTrue(registrationTokenService.tokenIsExpiredByDate(token1.getToken(), oneDayAfterRightNow));
    }

    @Test
    void testTokenIsExpiredByDateWhenOneMinutePassed () {
        List<RegistrationToken> tokens = getNewTokens();
        RegistrationToken token1 = tokens.get(0);
        when(registrationTokensRepository.findById(token1.getToken()))
                .thenReturn(Optional.of(token1));

        LocalDateTime rightNow = LocalDateTime.now().plusMinutes(1);
        assertFalse(registrationTokenService.tokenIsExpiredByDate(token1.getToken(), rightNow));
    }

    @Test
    void generateToken() {
        User user = TestUtils.getNewDefaultUser();
        RegistrationToken newToken = new RegistrationToken(
                "this-is-a-random-entry",
                user
        );
        when(registrationTokensRepository.save(any(RegistrationToken.class)))
                .thenReturn(newToken);

        String result = registrationTokenService.generateToken(user);
        assertEquals(newToken.getToken(), result);
    }


    private final List<User> userList = TestUtils.getNewUsers();
    private final List<RegistrationToken> tokensList = List.of(
            new RegistrationToken("token1", userList.get(0)),
            new RegistrationToken("token2", userList.get(1)),
            new RegistrationToken("token3", userList.get(2))
    );
    private List<RegistrationToken> getNewTokens() {
        return new ArrayList<>(tokensList);
    }
}